package com.zullid.apolo_music_bot.handlers;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import com.zullid.apolo_music_bot.player.Player;
import com.zullid.apolo_music_bot.player.state.ReadyState;
import com.zullid.apolo_music_bot.services.AudioPlayerService;
import com.zullid.apolo_music_bot.services.QueueService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AudioEventHandlerTest {

    @Mock
    private QueueService queueService;

    @Mock
    private AudioPlayerService audioPlayerService;

    @Mock
    private Player player;

    @Mock
    private AudioPlayer audioPlayer;

    @Mock
    private AudioTrack track;

    private AudioEventHandler handler;

    @BeforeEach
    void setUp() {
        handler = new AudioEventHandler(queueService, audioPlayerService, player);
    }

    @Test
    void onTrackEnd_withReasonFinished_playsNextTrack() {
        AudioTrackEndReason reason = AudioTrackEndReason.FINISHED;

        handler.onTrackEnd(audioPlayer, track, reason);

        verify(queueService).playNextTrack();
    }

    @Test
    void onTrackEnd_withReasonFinishedAndQueueEmpty_transitionsToReadyState() {
        AudioTrackEndReason reason = AudioTrackEndReason.FINISHED;
        when(queueService.isQueueEmpty()).thenReturn(true);
        when(audioPlayerService.getPlayer()).thenReturn(audioPlayer);
        when(audioPlayer.getPlayingTrack()).thenReturn(null);

        handler.onTrackEnd(audioPlayer, track, reason);

        verify(queueService).playNextTrack();
        verify(player).setState(any(ReadyState.class));
    }

    @Test
    void onTrackEnd_withReasonStopped_doesNotPlayNext() {
        AudioTrackEndReason reason = AudioTrackEndReason.STOPPED;

        handler.onTrackEnd(audioPlayer, track, reason);

        verify(queueService, never()).playNextTrack();
    }

    @Test
    void onTrackEnd_withReasonFinishedAndQueueNotEmpty_doesNotTransitionToReadyState() {
        AudioTrackEndReason reason = AudioTrackEndReason.FINISHED;
        when(queueService.isQueueEmpty()).thenReturn(false);
        when(audioPlayerService.getPlayer()).thenReturn(audioPlayer);
        AudioTrack nextTrack = mock(AudioTrack.class);
        when(audioPlayer.getPlayingTrack()).thenReturn(nextTrack);

        handler.onTrackEnd(audioPlayer, track, reason);

        verify(queueService).playNextTrack();
        verify(player, never()).setState(any(ReadyState.class));
    }

    @Test
    void onTrackEnd_withReasonReplaced_doesNotPlayNext() {
        AudioTrackEndReason reason = AudioTrackEndReason.REPLACED;

        handler.onTrackEnd(audioPlayer, track, reason);

        verify(queueService, never()).playNextTrack();
    }

    @Test
    void onTrackEnd_withReasonLoadFailed_playsNextTrack() {
        AudioTrackEndReason reason = AudioTrackEndReason.LOAD_FAILED;

        handler.onTrackEnd(audioPlayer, track, reason);

        verify(queueService).playNextTrack();
    }

    @Test
    void onTrackStart_logsTrackTitle() {
        when(track.getInfo()).thenReturn(mock(com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo.class));

        handler.onTrackStart(audioPlayer, track);
    }

    @Test
    void onPlayerPause_logsPauseMessage() {
        handler.onPlayerPause(audioPlayer);
    }

    @Test
    void onPlayerResume_logsResumeMessage() {
        handler.onPlayerResume(audioPlayer);
    }
}