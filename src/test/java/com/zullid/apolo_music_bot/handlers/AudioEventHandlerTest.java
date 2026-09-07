package com.zullid.apolo_music_bot.handlers;

import static org.mockito.Mockito.*;

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

/**
 * Unit tests for {@link AudioEventHandler}.
 * <p>
 * Verifies queue advancement on track end based on {@code AudioTrackEndReason}, the
 * transition back to {@code ReadyState} when drained, and that start, pause and resume
 * callbacks are handled, using mocked queue, player service and player context.
 * </p>
 *
 * @author Dante Zulli (dantezulli2004@gmail.com)
 */
@ExtendWith(MockitoExtension.class)
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

    /**
     * Creates the handler with mocked collaborators before each test.
     */
    @BeforeEach
    void setUp() {
        handler = new AudioEventHandler(
            queueService,
            audioPlayerService,
            player
        );
    }

    /**
     * Tests that a finished track advances the queue.
     * <p>
     * Given end reason {@code FINISHED}, when {@code onTrackEnd} runs, then the next track
     * is played.
     * </p>
     */
    @Test
    void onTrackEnd_withReasonFinished_playsNextTrack() {
        AudioTrackEndReason reason = AudioTrackEndReason.FINISHED;

        handler.onTrackEnd(audioPlayer, track, reason);

        verify(queueService).playNextTrack();
    }

    /**
     * Tests that draining the queue returns the player to ready state.
     * <p>
     * Given end reason {@code FINISHED} with an empty queue and nothing playing, when
     * {@code onTrackEnd} runs, then the next track is requested and the state becomes
     * {@code ReadyState}.
     * </p>
     */
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

    /**
     * Tests that a manually stopped track does not advance the queue.
     * <p>
     * Given end reason {@code STOPPED}, when {@code onTrackEnd} runs, then no next track
     * is played.
     * </p>
     */
    @Test
    void onTrackEnd_withReasonStopped_doesNotPlayNext() {
        AudioTrackEndReason reason = AudioTrackEndReason.STOPPED;

        handler.onTrackEnd(audioPlayer, track, reason);

        verify(queueService, never()).playNextTrack();
    }

    /**
     * Tests that a non-empty queue keeps the current state.
     * <p>
     * Given end reason {@code FINISHED} with tracks remaining, when {@code onTrackEnd}
     * runs, then the next track is played without transitioning to {@code ReadyState}.
     * </p>
     */
    @Test
    void onTrackEnd_withReasonFinishedAndQueueNotEmpty_doesNotTransitionToReadyState() {
        AudioTrackEndReason reason = AudioTrackEndReason.FINISHED;
        when(queueService.isQueueEmpty()).thenReturn(false);

        handler.onTrackEnd(audioPlayer, track, reason);

        verify(queueService).playNextTrack();
        verify(player, never()).setState(any(ReadyState.class));
    }

    /**
     * Tests that a replaced track does not advance the queue.
     * <p>
     * Given end reason {@code REPLACED}, when {@code onTrackEnd} runs, then no next track
     * is played.
     * </p>
     */
    @Test
    void onTrackEnd_withReasonReplaced_doesNotPlayNext() {
        AudioTrackEndReason reason = AudioTrackEndReason.REPLACED;

        handler.onTrackEnd(audioPlayer, track, reason);

        verify(queueService, never()).playNextTrack();
    }

    /**
     * Tests that a failed load still advances the queue.
     * <p>
     * Given end reason {@code LOAD_FAILED}, when {@code onTrackEnd} runs, then the next
     * track is played.
     * </p>
     */
    @Test
    void onTrackEnd_withReasonLoadFailed_playsNextTrack() {
        AudioTrackEndReason reason = AudioTrackEndReason.LOAD_FAILED;

        handler.onTrackEnd(audioPlayer, track, reason);

        verify(queueService).playNextTrack();
    }

    /**
     * Tests that track start is handled without errors.
     * <p>
     * Given a track with metadata, when {@code onTrackStart} runs, then it completes
     * (logging the title) without throwing.
     * </p>
     */
    @Test
    void onTrackStart_logsTrackTitle() {
        com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo info =
            new com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo(
                "Test Track",
                "test",
                0L,
                "id",
                false,
                null
            );
        when(track.getInfo()).thenReturn(info);

        handler.onTrackStart(audioPlayer, track);
    }

    /**
     * Tests that pause events are handled without errors.
     * <p>
     * Given any player, when {@code onPlayerPause} runs, then it completes without
     * throwing.
     * </p>
     */
    @Test
    void onPlayerPause_logsPauseMessage() {
        handler.onPlayerPause(audioPlayer);
    }

    /**
     * Tests that resume events are handled without errors.
     * <p>
     * Given any player, when {@code onPlayerResume} runs, then it completes without
     * throwing.
     * </p>
     */
    @Test
    void onPlayerResume_logsResumeMessage() {
        handler.onPlayerResume(audioPlayer);
    }
}
