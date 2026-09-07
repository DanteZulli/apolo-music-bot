package com.zullid.apolo_music_bot.player.state;

import static org.mockito.Mockito.*;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.zullid.apolo_music_bot.player.Player;
import com.zullid.apolo_music_bot.services.AudioPlayerService;
import com.zullid.apolo_music_bot.services.QueueService;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

/**
 * Unit tests for {@link PausedState}.
 * <p>
 * Verifies that resume unpauses and returns to playing, stop drains and readies, and play, pause and skip are rejected, using mocked services and player.
 * </p>
 *
 * @author Dante Zulli (dantezulli2004@gmail.com)
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PausedStateTest {

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

    private PausedState pausedState;

    /**
     * Creates the paused state with mocked services before each test.
     */
    @BeforeEach
    void setUp() {
        when(player.getAudioPlayerService()).thenReturn(audioPlayerService);
        when(player.getQueueService()).thenReturn(queueService);
        when(audioPlayerService.getPlayer()).thenReturn(audioPlayer);

        pausedState = new PausedState(player);

        lenient().when(event.reply(anyString())).thenReturn(replyAction);
    }

    /**
     * Tests that new playback is rejected while paused.
     * <p>
     * Given paused state, when play is received, then a use-resume reply is sent.
     * </p>
     */
    @Test
    void onPlay_repliesCannotPlayWhilePaused() {
        pausedState.onPlay(event);

        verify(event).reply("Cannot play while paused. Use resume.");
    }

    /**
     * Tests that double pause is rejected.
     * <p>
     * Given paused state, when pause is received, then an already-paused reply is sent.
     * </p>
     */
    @Test
    void onPause_repliesAlreadyPausedMessage() {
        pausedState.onPause(event);

        verify(event).reply("Player is already paused!");
    }

    /**
     * Tests that resume restores playback.
     * <p>
     * Given paused state, when resume is received, then the player unpauses and transitions to playing.
     * </p>
     */
    @Test
    void onResume_unpausesPlayerAndTransitionsToPlayingState() {
        pausedState.onResume(event);

        verify(audioPlayer).setPaused(false);
        verify(event).reply("Resumed the current track");
        verify(player).setState(any(PlayingState.class));
    }

    /**
     * Tests that stop drains and readies.
     * <p>
     * Given paused state, when stop is received, then the queue clears, the track stops and the state becomes ready.
     * </p>
     */
    @Test
    void onStop_clearsQueueStopsTrackAndTransitionsToReadyState() {
        pausedState.onStop(event);

        verify(queueService).clearQueue();
        verify(audioPlayer).stopTrack();
        verify(event).reply("Stopped playback and cleared the queue");
        verify(player).setState(any(ReadyState.class));
    }

    /**
     * Tests that skip is rejected while paused.
     * <p>
     * Given paused state, when skip is received, then a use-resume reply is sent.
     * </p>
     */
    @Test
    void onSkip_repliesCannotSkipWhilePaused() {
        pausedState.onSkip(event);

        verify(event).reply("Cannot skip while paused. Use resume.");
    }
}
