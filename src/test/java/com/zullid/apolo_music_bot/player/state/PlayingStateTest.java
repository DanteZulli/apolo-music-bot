package com.zullid.apolo_music_bot.player.state;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import com.zullid.apolo_music_bot.player.Player;
import com.zullid.apolo_music_bot.services.AudioPlayerService;
import com.zullid.apolo_music_bot.services.QueueService;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageCreateAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
    private WebhookMessageCreateAction<?> editAction;

    @Captor
    private ArgumentCaptor<AudioLoadResultHandler> handlerCaptor;

    private PlayingState playingState;

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
        lenient().when(hook.sendMessage(anyString())).thenReturn((WebhookMessageCreateAction) editAction);
        lenient().doNothing().when(editAction).queue();
    }

    @Test
    void onPlay_defersReplyAndLoadsItem() {
        when(optionMapping.getAsString()).thenReturn("some query");

        playingState.onPlay(event);

        verify(event).deferReply();
        verify(playerManager).loadItem(eq("some query"), handlerCaptor.capture());
    }

    @Test
    void onPlay_trackLoaded_addsToQueueAndReplies() {
        when(optionMapping.getAsString()).thenReturn("some query");
        AudioTrackInfo info = new AudioTrackInfo("Test Song", "test", 0L, "id", false, null);
        when(track.getInfo()).thenReturn(info);

        playingState.onPlay(event);
        verify(playerManager).loadItem(eq("some query"), handlerCaptor.capture());
        handlerCaptor.getValue().trackLoaded(track);

        verify(queueService).addToQueue(track);
        verify(hook).sendMessage("Added to queue: Test Song");
        verify(editAction).queue();
    }

    @Test
    void onPlay_playlistLoaded_searchResult_addsFirstTrack() {
        when(optionMapping.getAsString()).thenReturn("search query");
        when(playlist.isSearchResult()).thenReturn(true);
        when(playlist.getSelectedTrack()).thenReturn(null);
        AudioTrackInfo info = new AudioTrackInfo("Search Result", "test", 0L, "id", false, null);
        when(track.getInfo()).thenReturn(info);
        when(playlist.getTracks()).thenReturn(List.of(track));

        playingState.onPlay(event);
        verify(playerManager).loadItem(eq("search query"), handlerCaptor.capture());
        handlerCaptor.getValue().playlistLoaded(playlist);

        verify(queueService).addToQueue(track);
        verify(hook).sendMessage("Added to queue: Search Result");
    }

    @Test
    void onPlay_playlistLoaded_searchResultWithSelectedTrack_usesSelectedTrack() {
        when(optionMapping.getAsString()).thenReturn("search query");
        when(playlist.isSearchResult()).thenReturn(true);
        AudioTrackInfo info = new AudioTrackInfo("Selected Track", "test", 0L, "id", false, null);
        when(track.getInfo()).thenReturn(info);
        when(playlist.getSelectedTrack()).thenReturn(track);

        playingState.onPlay(event);
        verify(playerManager).loadItem(eq("search query"), handlerCaptor.capture());
        handlerCaptor.getValue().playlistLoaded(playlist);

        verify(queueService).addToQueue(track);
        verify(hook).sendMessage("Added to queue: Selected Track");
    }

    @Test
    void onPlay_playlistLoaded_regularPlaylist_addsAllTracks() {
        when(optionMapping.getAsString()).thenReturn("https://youtube.com/playlist?list=PLabc");
        when(playlist.isSearchResult()).thenReturn(false);
        AudioTrackInfo info1 = new AudioTrackInfo("Track 1", "test", 0L, "id1", false, null);
        AudioTrackInfo info2 = new AudioTrackInfo("Track 2", "test", 0L, "id2", false, null);
        AudioTrack track1 = mock(AudioTrack.class);
        AudioTrack track2 = mock(AudioTrack.class);
        when(track1.getInfo()).thenReturn(info1);
        when(track2.getInfo()).thenReturn(info2);
        when(playlist.getTracks()).thenReturn(List.of(track1, track2));
        when(playlist.getSelectedTrack()).thenReturn(null);

        playingState.onPlay(event);
        verify(playerManager).loadItem(eq("https://youtube.com/playlist?list=PLabc"), handlerCaptor.capture());
        handlerCaptor.getValue().playlistLoaded(playlist);

        verify(queueService).addToQueue(track1);
        verify(queueService).addToQueue(track2);
        verify(hook).sendMessage("Added 2 tracks to queue");
    }

    @Test
    void onPlay_playlistLoaded_automaticMix_addsFirstTrack() {
        when(optionMapping.getAsString()).thenReturn("some song list=RDmix");
        when(playlist.isSearchResult()).thenReturn(false);
        when(playlist.getSelectedTrack()).thenReturn(null);
        AudioTrackInfo info = new AudioTrackInfo("Auto Mix Track", "test", 0L, "id", false, null);
        when(track.getInfo()).thenReturn(info);
        when(playlist.getTracks()).thenReturn(List.of(track));

        playingState.onPlay(event);
        verify(playerManager).loadItem(eq("some song list=RDmix"), handlerCaptor.capture());
        handlerCaptor.getValue().playlistLoaded(playlist);

        verify(queueService).addToQueue(track);
        verify(hook).sendMessage("Added to queue: Auto Mix Track");
    }

    @Test
    void onPlay_noMatches_repliesErrorAndTransitionsToReadyState() {
        when(optionMapping.getAsString()).thenReturn("nonexistent");

        playingState.onPlay(event);
        verify(playerManager).loadItem(eq("nonexistent"), handlerCaptor.capture());
        handlerCaptor.getValue().noMatches();

        verify(hook).sendMessage("No matches found for: nonexistent");
        verify(player).setState(any(ReadyState.class));
    }

    @Test
    void onPlay_loadFailed_repliesErrorAndTransitionsToReadyState() {
        when(optionMapping.getAsString()).thenReturn("bad query");
        when(exception.getMessage()).thenReturn("Something went wrong");

        playingState.onPlay(event);
        verify(playerManager).loadItem(eq("bad query"), handlerCaptor.capture());
        handlerCaptor.getValue().loadFailed(exception);

        verify(hook).sendMessage("Error loading track: Something went wrong");
        verify(player).setState(any(ReadyState.class));
    }

    @Test
    void onPause_setsPlayerPausedAndTransitionsToPausedState() {
        playingState.onPause(event);

        verify(audioPlayer).setPaused(true);
        verify(event).reply("Paused the current track");
        verify(player).setState(any(PausedState.class));
    }

    @Test
    void onResume_repliesCannotResumeMessage() {
        playingState.onResume(event);

        verify(event).reply("Cannot resume: player is already playing.");
    }

    @Test
    void onStop_clearsQueueStopsTrackAndTransitionsToReadyState() {
        playingState.onStop(event);

        verify(queueService).clearQueue();
        verify(audioPlayer).stopTrack();
        verify(event).reply("Stopped playback and cleared the queue");
        verify(player).setState(any(ReadyState.class));
    }

    @Test
    void onSkip_skipsCurrentTrackAndReplies() {
        playingState.onSkip(event);

        verify(queueService).skipCurrentTrack();
        verify(event).reply("Skipped to the next track");
    }
}