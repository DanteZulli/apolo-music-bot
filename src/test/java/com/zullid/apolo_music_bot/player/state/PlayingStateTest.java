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
 * playlists and automatic {@code list=RD} mixes), full-queue reporting, error paths
 * returning to ready state,
 * plus pause, resume, stop and skip behavior, using mocked player services and events.
 * </p>
 *
 * @author Dante Zulli (dantezulli2004@gmail.com)
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PlayingStateTest {

    private static final String OPTION_QUERY = "query";
    private static final String DEFAULT_QUERY = "test-query";
    private static final String QUERY_SOME = "some query";
    private static final String QUERY_SEARCH = "search query";
    private static final String QUERY_PLAYLIST =
        "https://youtube.com/playlist?list=PLabc";
    private static final String QUERY_AUTOMIX = "some song list=RDmix";
    private static final String QUERY_MISSING = "nonexistent";
    private static final String QUERY_BAD = "bad query";
    private static final String TRACK_AUTHOR = "test";
    private static final String TRACK_ID = "id";
    private static final String TRACK_ID_1 = "id1";
    private static final String TRACK_ID_2 = "id2";
    private static final String TITLE_TEST_SONG = "Test Song";
    private static final String TITLE_SEARCH_RESULT = "Search Result";
    private static final String TITLE_SELECTED = "Selected Track";
    private static final String TITLE_TRACK_1 = "Track 1";
    private static final String TITLE_TRACK_2 = "Track 2";
    private static final String TITLE_AUTOMIX = "Auto Mix Track";
    private static final String ADDED_SINGLE_FORMAT = "Added to queue: %s";
    private static final String MSG_QUEUE_FULL = "Queue is full (max 50 tracks)";
    private static final String MSG_ADDED_TWO = "Added 2 tracks to queue";
    private static final String MSG_ADDED_PARTIAL =
        "Added 1 of 2 tracks to queue (queue is full, max 50)";
    private static final String NO_MATCHES_FORMAT = "No matches found for: %s";
    private static final String LOAD_FAILED_FORMAT = "Error loading track: %s";
    private static final String LOAD_FAILURE_DETAIL = "Something went wrong";
    private static final String MSG_PAUSED = "Paused the current track";
    private static final String MSG_CANNOT_RESUME =
        "Cannot resume: player is already playing.";
    private static final String MSG_STOPPED =
        "Stopped playback and cleared the queue";
    private static final String MSG_SKIPPED = "Skipped to the next track";

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
        lenient().when(event.getOption(OPTION_QUERY)).thenReturn(optionMapping);
        lenient().when(optionMapping.getAsString()).thenReturn(DEFAULT_QUERY);
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
        when(optionMapping.getAsString()).thenReturn(QUERY_SOME);

        playingState.onPlay(event);

        verify(event).deferReply();
        verify(playerManager).loadItem(
            eq(QUERY_SOME),
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
        when(optionMapping.getAsString()).thenReturn(QUERY_SOME);
        AudioTrackInfo info = new AudioTrackInfo(
            TITLE_TEST_SONG,
            TRACK_AUTHOR,
            0L,
            TRACK_ID,
            false,
            null
        );
        when(track.getInfo()).thenReturn(info);

        playingState.onPlay(event);
        verify(playerManager).loadItem(
            eq(QUERY_SOME),
            handlerCaptor.capture()
        );
        when(queueService.addToQueue(track)).thenReturn(true);
        handlerCaptor.getValue().trackLoaded(track);

        verify(queueService).addToQueue(track);
        verify(hook).sendMessage(ADDED_SINGLE_FORMAT.formatted(TITLE_TEST_SONG));
        verify(editAction).queue();
    }

    /**
     * Tests that a single track rejected by a full queue is reported.
     * <p>
     * Given a full queue, when the load callback fires, then a queue-full message
     * is sent instead of the added confirmation.
     * </p>
     */
    @Test
    void onPlay_trackLoaded_whenQueueFull_repliesQueueFull() {
        when(optionMapping.getAsString()).thenReturn(QUERY_SOME);
        AudioTrackInfo info = new AudioTrackInfo(
            TITLE_TEST_SONG,
            TRACK_AUTHOR,
            0L,
            TRACK_ID,
            false,
            null
        );
        when(track.getInfo()).thenReturn(info);

        playingState.onPlay(event);
        verify(playerManager).loadItem(
            eq(QUERY_SOME),
            handlerCaptor.capture()
        );
        when(queueService.addToQueue(track)).thenReturn(false);
        handlerCaptor.getValue().trackLoaded(track);

        verify(hook).sendMessage(MSG_QUEUE_FULL);
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
        when(optionMapping.getAsString()).thenReturn(QUERY_SEARCH);
        when(playlist.isSearchResult()).thenReturn(true);
        when(playlist.getSelectedTrack()).thenReturn(null);
        AudioTrackInfo info = new AudioTrackInfo(
            TITLE_SEARCH_RESULT,
            TRACK_AUTHOR,
            0L,
            TRACK_ID,
            false,
            null
        );
        when(track.getInfo()).thenReturn(info);
        when(playlist.getTracks()).thenReturn(List.of(track));

        playingState.onPlay(event);
        verify(playerManager).loadItem(
            eq(QUERY_SEARCH),
            handlerCaptor.capture()
        );
        when(queueService.addToQueue(track)).thenReturn(true);
        handlerCaptor.getValue().playlistLoaded(playlist);

        verify(queueService).addToQueue(track);
        verify(hook).sendMessage(
            ADDED_SINGLE_FORMAT.formatted(TITLE_SEARCH_RESULT)
        );
    }

    /**
     * Tests that selected search tracks are preferred.
     * <p>
     * Given a search-result playlist with selection, when loaded, then the selected track is queued.
     * </p>
     */
    @Test
    void onPlay_playlistLoaded_searchResultWithSelectedTrack_usesSelectedTrack() {
        when(optionMapping.getAsString()).thenReturn(QUERY_SEARCH);
        when(playlist.isSearchResult()).thenReturn(true);
        AudioTrackInfo info = new AudioTrackInfo(
            TITLE_SELECTED,
            TRACK_AUTHOR,
            0L,
            TRACK_ID,
            false,
            null
        );
        when(track.getInfo()).thenReturn(info);
        when(playlist.getSelectedTrack()).thenReturn(track);

        playingState.onPlay(event);
        verify(playerManager).loadItem(
            eq(QUERY_SEARCH),
            handlerCaptor.capture()
        );
        when(queueService.addToQueue(track)).thenReturn(true);
        handlerCaptor.getValue().playlistLoaded(playlist);

        verify(queueService).addToQueue(track);
        verify(hook).sendMessage(
            ADDED_SINGLE_FORMAT.formatted(TITLE_SELECTED)
        );
    }

    /**
     * Tests that manual playlists queue every track.
     * <p>
     * Given a manual playlist with two tracks, when loaded, then both are queued and counted.
     * </p>
     */
    @Test
    void onPlay_playlistLoaded_regularPlaylist_addsAllTracks() {
        when(optionMapping.getAsString()).thenReturn(QUERY_PLAYLIST);
        when(playlist.isSearchResult()).thenReturn(false);
        AudioTrackInfo info1 = new AudioTrackInfo(
            TITLE_TRACK_1,
            TRACK_AUTHOR,
            0L,
            TRACK_ID_1,
            false,
            null
        );
        AudioTrackInfo info2 = new AudioTrackInfo(
            TITLE_TRACK_2,
            TRACK_AUTHOR,
            0L,
            TRACK_ID_2,
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
            eq(QUERY_PLAYLIST),
            handlerCaptor.capture()
        );
        when(queueService.addToQueue(track1)).thenReturn(true);
        when(queueService.addToQueue(track2)).thenReturn(true);
        handlerCaptor.getValue().playlistLoaded(playlist);

        verify(queueService).addToQueue(track1);
        verify(queueService).addToQueue(track2);
        verify(hook).sendMessage(MSG_ADDED_TWO);
    }

    /**
     * Tests that a partially queued playlist reports the skipped tracks.
     * <p>
     * Given a manual playlist where the queue fills up midway, when loaded, then
     * the reply reports how many tracks were added out of the total.
     * </p>
     */
    @Test
    void onPlay_playlistLoaded_regularPlaylist_whenQueueFull_reportsPartialAdd() {
        when(optionMapping.getAsString()).thenReturn(QUERY_PLAYLIST);
        when(playlist.isSearchResult()).thenReturn(false);
        AudioTrackInfo info1 = new AudioTrackInfo(
            TITLE_TRACK_1,
            TRACK_AUTHOR,
            0L,
            TRACK_ID_1,
            false,
            null
        );
        AudioTrackInfo info2 = new AudioTrackInfo(
            TITLE_TRACK_2,
            TRACK_AUTHOR,
            0L,
            TRACK_ID_2,
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
            eq(QUERY_PLAYLIST),
            handlerCaptor.capture()
        );
        when(queueService.addToQueue(track1)).thenReturn(true);
        when(queueService.addToQueue(track2)).thenReturn(false);
        handlerCaptor.getValue().playlistLoaded(playlist);

        verify(queueService).addToQueue(track1);
        verify(queueService).addToQueue(track2);
        verify(hook).sendMessage(MSG_ADDED_PARTIAL);
    }

    /**
     * Tests that automatic mixes queue only one track.
     * <p>
     * Given a non-search playlist with {@code list=RD} query, when loaded, then only the first track is queued to avoid radio flooding.
     * </p>
     */
    @Test
    void onPlay_playlistLoaded_automaticMix_addsFirstTrack() {
        when(optionMapping.getAsString()).thenReturn(QUERY_AUTOMIX);
        when(playlist.isSearchResult()).thenReturn(false);
        when(playlist.getSelectedTrack()).thenReturn(null);
        AudioTrackInfo info = new AudioTrackInfo(
            TITLE_AUTOMIX,
            TRACK_AUTHOR,
            0L,
            TRACK_ID,
            false,
            null
        );
        when(track.getInfo()).thenReturn(info);
        when(playlist.getTracks()).thenReturn(List.of(track));

        playingState.onPlay(event);
        verify(playerManager).loadItem(
            eq(QUERY_AUTOMIX),
            handlerCaptor.capture()
        );
        when(queueService.addToQueue(track)).thenReturn(true);
        handlerCaptor.getValue().playlistLoaded(playlist);

        verify(queueService).addToQueue(track);
        verify(hook).sendMessage(
            ADDED_SINGLE_FORMAT.formatted(TITLE_AUTOMIX)
        );
    }

    /**
     * Tests that no matches return to ready state.
     * <p>
     * Given no load matches, when the callback fires, then an error is sent and the state becomes ready.
     * </p>
     */
    @Test
    void onPlay_noMatches_repliesErrorAndTransitionsToReadyState() {
        when(optionMapping.getAsString()).thenReturn(QUERY_MISSING);

        playingState.onPlay(event);
        verify(playerManager).loadItem(
            eq(QUERY_MISSING),
            handlerCaptor.capture()
        );
        handlerCaptor.getValue().noMatches();

        verify(hook).sendMessage(NO_MATCHES_FORMAT.formatted(QUERY_MISSING));
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
        when(optionMapping.getAsString()).thenReturn(QUERY_BAD);
        when(exception.getMessage()).thenReturn(LOAD_FAILURE_DETAIL);

        playingState.onPlay(event);
        verify(playerManager).loadItem(
            eq(QUERY_BAD),
            handlerCaptor.capture()
        );
        handlerCaptor.getValue().loadFailed(exception);

        verify(hook).sendMessage(
            LOAD_FAILED_FORMAT.formatted(LOAD_FAILURE_DETAIL)
        );
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
        verify(event).reply(MSG_PAUSED);
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

        verify(event).reply(MSG_CANNOT_RESUME);
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
        verify(event).reply(MSG_STOPPED);
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
        verify(event).reply(MSG_SKIPPED);
    }
}
