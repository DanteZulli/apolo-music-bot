package com.zullid.apolo_music_bot.player.state;

import static org.mockito.Mockito.*;

import com.zullid.apolo_music_bot.player.Player;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for {@link ReadyState}.
 * <p>
 * Verifies that play transitions to playing while pause, resume, stop and skip are rejected with guidance messages, using mocked player and events.
 * </p>
 *
 * @author Dante Zulli (dantezulli2004@gmail.com)
 */
@ExtendWith(MockitoExtension.class)
class ReadyStateTest {

    private static final String MSG_CANNOT_PAUSE =
        "Cannot pause: nothing is playing.";
    private static final String MSG_CANNOT_RESUME =
        "Cannot resume: nothing is playing.";
    private static final String MSG_ALREADY_STOPPED = "Already stopped.";
    private static final String MSG_CANNOT_SKIP =
        "Cannot skip: nothing is playing.";

    @Mock
    private Player player;

    @Mock
    private SlashCommandInteractionEvent event;

    @Mock
    private ReplyCallbackAction replyAction;

    private ReadyState readyState;

    /**
     * Creates the ready state with a mocked player before each test.
     */
    @BeforeEach
    void setUp() {
        readyState = new ReadyState(player);
        lenient().when(event.reply(anyString())).thenReturn(replyAction);
    }

    /**
     * Tests that play leaves the idle state.
     * <p>
     * Given ready state, when play is received, then it transitions to playing and delegates playback.
     * </p>
     */
    @Test
    void onPlay_transitionsToPlayingState() {
        readyState.onPlay(event);

        verify(player).setState(any(PlayingState.class));
        verify(player).play(event);
    }

    /**
     * Tests that pause is rejected while idle.
     * <p>
     * Given ready state, when pause is received, then a cannot-pause reply is sent.
     * </p>
     */
    @Test
    void onPause_repliesCannotPauseMessage() {
        readyState.onPause(event);

        verify(event).reply(MSG_CANNOT_PAUSE);
    }

    /**
     * Tests that resume is rejected while idle.
     * <p>
     * Given ready state, when resume is received, then a cannot-resume reply is sent.
     * </p>
     */
    @Test
    void onResume_repliesCannotResumeMessage() {
        readyState.onResume(event);

        verify(event).reply(MSG_CANNOT_RESUME);
    }

    /**
     * Tests that stop is acknowledged as already stopped.
     * <p>
     * Given ready state, when stop is received, then an already-stopped reply is sent.
     * </p>
     */
    @Test
    void onStop_repliesAlreadyStoppedMessage() {
        readyState.onStop(event);

        verify(event).reply(MSG_ALREADY_STOPPED);
    }

    /**
     * Tests that skip is rejected while idle.
     * <p>
     * Given ready state, when skip is received, then a cannot-skip reply is sent.
     * </p>
     */
    @Test
    void onSkip_repliesCannotSkipMessage() {
        readyState.onSkip(event);

        verify(event).reply(MSG_CANNOT_SKIP);
    }
}
