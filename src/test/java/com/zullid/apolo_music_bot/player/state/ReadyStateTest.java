package com.zullid.apolo_music_bot.player.state;

import com.zullid.apolo_music_bot.player.Player;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReadyStateTest {

    @Mock
    private Player player;

    @Mock
    private SlashCommandInteractionEvent event;

    @Mock
    private ReplyCallbackAction replyAction;

    private ReadyState readyState;

    @BeforeEach
    void setUp() {
        readyState = new ReadyState(player);
        lenient().when(event.reply(anyString())).thenReturn(replyAction);
    }

    @Test
    void onPlay_transitionsToPlayingState() {
        readyState.onPlay(event);

        verify(player).setState(any(PlayingState.class));
        verify(player).play(event);
    }

    @Test
    void onPause_repliesCannotPauseMessage() {
        readyState.onPause(event);

        verify(event).reply("Cannot pause: nothing is playing.");
    }

    @Test
    void onResume_repliesCannotResumeMessage() {
        readyState.onResume(event);

        verify(event).reply("Cannot resume: nothing is playing.");
    }

    @Test
    void onStop_repliesAlreadyStoppedMessage() {
        readyState.onStop(event);

        verify(event).reply("Already stopped.");
    }

    @Test
    void onSkip_repliesCannotSkipMessage() {
        readyState.onSkip(event);

        verify(event).reply("Cannot skip: nothing is playing.");
    }
}