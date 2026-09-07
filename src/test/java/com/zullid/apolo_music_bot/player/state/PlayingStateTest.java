package com.zullid.apolo_music_bot.player.state;

import static org.mockito.Mockito.*;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import com.zullid.apolo_music_bot.player.Player;
import com.zullid.apolo_music_bot.services.AudioPlayerService;
import com.zullid.apolo_music_bot.services.QueueService;
import java.util.List;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageCreateAction;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

/**
 * Unit tests for {@link PlayingState}.
 * <p>
 * Verifies track and playlist loading (search results, selected tracks, full manual
 * playlists and automatic {@code list=RD} mixes), error paths returning to ready state,
 * plus pause, resume, stop and skip behavior, using mocked player services and events.
 * </p>
 *
 * @author Dante Zulli (dantezulli2004@gmail.com)
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PlayingStateTest {

    @Mock
    private Player player;

    @Mock
    private SlashCommandInteractionEvent event;

    @Mock
    private ReplyCallbackAction replyAction;

    @Mock
    private AudioPlayerService audioPlayerService;

    @Mock
    private QueueService queueService;

    @Mock
    private AudioPlayer audioPlayer;

    @Mock
    private AudioPlayerManager playerManager;

    @Mock
    private InteractionHook hook;

    @Mock
    private AudioTrack track;

    @Mock
    private AudioPlaylist playlist;

    @Mock
    private FriendlyException exception;

    @Mock
    private OptionMapping optionMapping;

    @Mock
    private WebhookMessageCreateAction<Message> editAction;

    @Captor
    private ArgumentCaptor<AudioLoadResultHandler> handlerCaptor;

    private PlayingState playingState;

    /**
     * Wires mocked player services and default event stubs before each test.
     */
    @BeforeEach
    void setUp() {
        when(player.getAudioPlayerService()).thenReturn(audioPlayerService);
        when(player.getQueueService()).thenReturn(queueService);
        when(audioPlayerService.getPlayer()).thenReturn(audioPlayer);
        when(audioPlayerService.getPlayerManager()).thenReturn(playerManager);

        playingState = new PlayingState(player);

        lenient().when(event.reply(anyString())).thenReturn(replyAction);
        lenient().when(event.deferReply()).thenReturn(replyAction);
        lenient().when(event.getOption("query")).thenReturn(optionMapping);
        lenient().when(optionMapping.getAsString()).thenReturn("test-query");
        lenient().when(event.getHook()).thenReturn(hook);
        lenient().doReturn(editAction).when(hook).sendMessage(anyString());
        lenient().doNothing().when(editAction).queue();
    }

    /**
     * Tests that play defers the reply and loads the query.
     * <p>
     * Given a query option, when play is received, then the reply is deferred and the item is loaded.
     * </p>
     */
    @Test
    void onPlay_defersReplyAndLoadsItem() {
        when(optionMapping.getAsString()).thenReturn("some query");

        playingState.onPlay(event);

        verify(event).deferReply();
        verify(playerManager).loadItem(
            eq("some query"),
            handlerCaptor.capture()
        );
    }

    /**
     * Tests that a single loaded track is queued.
     * <p>
     * Given a resolved track, when the load callback fires, then it is queued and confirmed.
     * </p>
     */
    @Test
    void onPlay_trackLoaded_addsToQueueAndReplies() {
        when(optionMapping.getAsString()).thenReturn("some query");
        AudioTrackInfo info = new AudioTrackInfo(
            "Test Song",
            "test",
            0L,
            "id",
            false,
            null
        );
        when(track.getInfo()).thenReturn(info);

        playingState.onPlay(event);
        verify(playerManager).loadItem(
            eq("some query"),
            handlerCaptor.capture()
        );
        handlerCaptor.getValue().trackLoaded(track);

        verify(queueService).addToQueue(track);
        verify(hook).sendMessage("Added to queue: Test Song");
        verify(editAction).queue();
    }

    /**
     * Tests that search results queue only the first track.
     * <p>
     * Given a search-result playlist without selection, when loaded, then the first track is queued.
     * </p>
     */
    @Test
    void onPlay_playlistLoaded_searchResult_addsFirstTrack() {
        when(optionMapping.getAsString()).thenReturn("search query");
        when(playlist.isSearchResult()).thenReturn(true);
        when(playlist.getSelectedTrack()).thenReturn(null);
        AudioTrackInfo info = new AudioTrackInfo(
            "Search Result",
            "test",
            0L,
            "id",
            false,
            null
        );
        when(track.getInfo()).thenReturn(info);
        when(playlist.getTracks()).thenReturn(List.of(track));

        playingState.onPlay(event);
        verify(playerManager).loadItem(
            eq("search query"),
            handlerCaptor.capture()
        );
        handlerCaptor.getValue().playlistLoaded(playlist);

        verify(queueService).addToQueue(track);
        verify(hook).sendMessage("Added to queue: Search Result");
    }

    /**
     * Tests that selected search tracks are preferred.
     * <p>
     * Given a search-result playlist with selection, when loaded, then the selected track is queued.
     * </p>
     */
    @Test
    void onPlay_playlistLoaded_searchResultWithSelectedTrack_usesSelectedTrack() {
        when(optionMapping.getAsString()).thenReturn("search query");
        when(playlist.isSearchResult()).thenReturn(true);
        AudioTrackInfo info = new AudioTrackInfo(
            "Selected Track",
            "test",
            0L,
            "id",
            false,
            null
        );
        when(track.getInfo()).thenReturn(info);
        when(playlist.getSelectedTrack()).thenReturn(track);

        playingState.onPlay(event);
        verify(playerManager).loadItem(
            eq("search query"),
            handlerCaptor.capture()
        );
        handlerCaptor.getValue().playlistLoaded(playlist);

        verify(queueService).addToQueue(track);
        verify(hook).sendMessage("Added to queue: Selected Track");
    }

    /**
     * Tests that manual playlists queue every track.
     * <p>
     * Given a manual playlist with two tracks, when loaded, then both are queued and counted.
     * </p>
     */
    @Test
    void onPlay_playlistLoaded_regularPlaylist_addsAllTracks() {
        when(optionMapping.getAsString()).thenReturn(
            "https://youtube.com/playlist?list=PLabc"
        );
        when(playlist.isSearchResult()).thenReturn(false);
        AudioTrackInfo info1 = new AudioTrackInfo(
            "Track 1",
            "test",
            0L,
            "id1",
            false,
            null
        );
        AudioTrackInfo info2 = new AudioTrackInfo(
            "Track 2",
            "test",
            0L,
            "id2",
            false,
            null
        );
        AudioTrack track1 = mock(AudioTrack.class);
        AudioTrack track2 = mock(AudioTrack.class);
        when(track1.getInfo()).thenReturn(info1);
        when(track2.getInfo()).thenReturn(info2);
        when(playlist.getTracks()).thenReturn(List.of(track1, track2));
        when(playlist.getSelectedTrack()).thenReturn(null);

        playingState.onPlay(event);
        verify(playerManager).loadItem(
            eq("https://youtube.com/playlist?list=PLabc"),
            handlerCaptor.capture()
        );
        handlerCaptor.getValue().playlistLoaded(playlist);

        verify(queueService).addToQueue(track1);
        verify(queueService).addToQueue(track2);
        verify(hook).sendMessage("Added 2 tracks to queue");
    }

    /**
     * Tests that automatic mixes queue only one track.
     * <p>
     * Given a non-search playlist with {@code list=RD} query, when loaded, then only the first track is queued to avoid radio flooding.
     * </p>
     */
    @Test
    void onPlay_playlistLoaded_automaticMix_addsFirstTrack() {
        when(optionMapping.getAsString()).thenReturn("some song list=RDmix");
        when(playlist.isSearchResult()).thenReturn(false);
        when(playlist.getSelectedTrack()).thenReturn(null);
        AudioTrackInfo info = new AudioTrackInfo(
            "Auto Mix Track",
            "test",
            0L,
            "id",
            false,
            null
        );
        when(track.getInfo()).thenReturn(info);
        when(playlist.getTracks()).thenReturn(List.of(track));

        playingState.onPlay(event);
        verify(playerManager).loadItem(
            eq("some song list=RDmix"),
            handlerCaptor.capture()
        );
        handlerCaptor.getValue().playlistLoaded(playlist);

        verify(queueService).addToQueue(track);
        verify(hook).sendMessage("Added to queue: Auto Mix Track");
    }

    /**
     * Tests that no matches return to ready state.
     * <p>
     * Given no load matches, when the callback fires, then an error is sent and the state becomes ready.
     * </p>
     */
    @Test
    void onPlay_noMatches_repliesErrorAndTransitionsToReadyState() {
        when(optionMapping.getAsString()).thenReturn("nonexistent");

        playingState.onPlay(event);
        verify(playerManager).loadItem(
            eq("nonexistent"),
            handlerCaptor.capture()
        );
        handlerCaptor.getValue().noMatches();

        verify(hook).sendMessage("No matches found for: nonexistent");
        verify(player).setState(any(ReadyState.class));
    }

    /**
     * Tests that load failures return to ready state.
     * <p>
     * Given a load exception, when the callback fires, then an error is sent and the state becomes ready.
     * </p>
     */
    @Test
    void onPlay_loadFailed_repliesErrorAndTransitionsToReadyState() {
        when(optionMapping.getAsString()).thenReturn("bad query");
        when(exception.getMessage()).thenReturn("Something went wrong");

        playingState.onPlay(event);
        verify(playerManager).loadItem(
            eq("bad query"),
            handlerCaptor.capture()
        );
        handlerCaptor.getValue().loadFailed(exception);

        verify(hook).sendMessage("Error loading track: Something went wrong");
        verify(player).setState(any(ReadyState.class));
    }

    /**
     * Tests that pause suspends and transitions.
     * <p>
     * Given playing state, when pause is received, then the player pauses and the state becomes paused.
     * </p>
     */
    @Test
    void onPause_setsPlayerPausedAndTransitionsToPausedState() {
        playingState.onPause(event);

        verify(audioPlayer).setPaused(true);
        verify(event).reply("Paused the current track");
        verify(player).setState(any(PausedState.class));
    }

    /**
     * Tests that resume is rejected while playing.
     * <p>
     * Given playing state, when resume is received, then a cannot-resume reply is sent.
     * </p>
     */
    @Test
    void onResume_repliesCannotResumeMessage() {
        playingState.onResume(event);

        verify(event).reply("Cannot resume: player is already playing.");
    }

    /**
     * Tests that stop drains and readies.
     * <p>
     * Given playing state, when stop is received, then the queue clears, the track stops and the state becomes ready.
     * </p>
     */
    @Test
    void onStop_clearsQueueStopsTrackAndTransitionsToReadyState() {
        playingState.onStop(event);

        verify(queueService).clearQueue();
        verify(audioPlayer).stopTrack();
        verify(event).reply("Stopped playback and cleared the queue");
        verify(player).setState(any(ReadyState.class));
    }

    /**
     * Tests that skip advances the queue.
     * <p>
     * Given playing state, when skip is received, then the current track is skipped.
     * </p>
     */
    @Test
    void onSkip_skipsCurrentTrackAndReplies() {
        playingState.onSkip(event);

        verify(queueService).skipCurrentTrack();
        verify(event).reply("Skipped to the next track");
    }
}
