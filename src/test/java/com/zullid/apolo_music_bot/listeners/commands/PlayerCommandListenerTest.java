package com.zullid.apolo_music_bot.listeners.commands;

import static org.mockito.Mockito.*;

import com.zullid.apolo_music_bot.player.Player;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for {@link PlayerCommandListener}.
 * <p>
 * Verifies that guild slash commands are routed to the matching {@link Player} method,
 * that unknown commands receive an ephemeral error, and that non-guild events are
 * ignored, using mocked player and interaction events.
 * </p>
 *
 * @author Dante Zulli (dantezulli2004@gmail.com)
 */
@ExtendWith(MockitoExtension.class)
class PlayerCommandListenerTest {

    @Mock
    private Player player;

    @Mock
    private SlashCommandInteractionEvent event;

    private PlayerCommandListener listener;

    /**
     * Creates the listener with a mocked player before each test.
     */
    @BeforeEach
    void setUp() {
        listener = new PlayerCommandListener(player);
    }

    /**
     * Tests that the play command is delegated to the player.
     * <p>
     * Given a guild {@code play} interaction, when handled, then {@code player.play} is
     * invoked.
     * </p>
     */
    @Test
    void onSlashCommandInteraction_playCommand_delegatesToPlayer() {
        when(event.isFromGuild()).thenReturn(true);
        when(event.getName()).thenReturn("play");

        listener.onSlashCommandInteraction(event);

        verify(player).play(event);
    }

    /**
     * Tests that the pause command is delegated to the player.
     * <p>
     * Given a guild {@code pause} interaction, when handled, then {@code player.pause} is
     * invoked.
     * </p>
     */
    @Test
    void onSlashCommandInteraction_pauseCommand_delegatesToPlayer() {
        when(event.isFromGuild()).thenReturn(true);
        when(event.getName()).thenReturn("pause");

        listener.onSlashCommandInteraction(event);

        verify(player).pause(event);
    }

    /**
     * Tests that the resume command is delegated to the player.
     * <p>
     * Given a guild {@code resume} interaction, when handled, then {@code player.resume} is
     * invoked.
     * </p>
     */
    @Test
    void onSlashCommandInteraction_resumeCommand_delegatesToPlayer() {
        when(event.isFromGuild()).thenReturn(true);
        when(event.getName()).thenReturn("resume");

        listener.onSlashCommandInteraction(event);

        verify(player).resume(event);
    }

    /**
     * Tests that the stop command is delegated to the player.
     * <p>
     * Given a guild {@code stop} interaction, when handled, then {@code player.stop} is
     * invoked.
     * </p>
     */
    @Test
    void onSlashCommandInteraction_stopCommand_delegatesToPlayer() {
        when(event.isFromGuild()).thenReturn(true);
        when(event.getName()).thenReturn("stop");

        listener.onSlashCommandInteraction(event);

        verify(player).stop(event);
    }

    /**
     * Tests that the skip command is delegated to the player.
     * <p>
     * Given a guild {@code skip} interaction, when handled, then {@code player.skip} is
     * invoked.
     * </p>
     */
    @Test
    void onSlashCommandInteraction_skipCommand_delegatesToPlayer() {
        when(event.isFromGuild()).thenReturn(true);
        when(event.getName()).thenReturn("skip");

        listener.onSlashCommandInteraction(event);

        verify(player).skip(event);
    }

    /**
     * Tests that the help command is delegated to the player.
     * <p>
     * Given a guild {@code help} interaction, when handled, then {@code player.help} is
     * invoked.
     * </p>
     */
    @Test
    void onSlashCommandInteraction_helpCommand_delegatesToPlayer() {
        when(event.isFromGuild()).thenReturn(true);
        when(event.getName()).thenReturn("help");

        listener.onSlashCommandInteraction(event);

        verify(player).help(event);
    }

    /**
     * Tests that the queue command is delegated to the player.
     * <p>
     * Given a guild {@code queue} interaction, when handled, then {@code player.queue} is
     * invoked.
     * </p>
     */
    @Test
    void onSlashCommandInteraction_queueCommand_delegatesToPlayer() {
        when(event.isFromGuild()).thenReturn(true);
        when(event.getName()).thenReturn("queue");

        listener.onSlashCommandInteraction(event);

        verify(player).queue(event);
    }

    /**
     * Tests that unknown commands receive an error reply.
     * <p>
     * Given a guild interaction with an unrecognized name, when handled, then no player
     * method is invoked and a {@code Command not recognized} reply is sent.
     * </p>
     */
    @Test
    void onSlashCommandInteraction_unknownCommand_repliesNotRecognized() {
        when(event.isFromGuild()).thenReturn(true);
        when(event.getName()).thenReturn("unknown");

        net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction replyAction =
            mock(
                net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction.class
            );
        when(event.reply(anyString())).thenReturn(replyAction);
        when(replyAction.setEphemeral(anyBoolean())).thenReturn(replyAction);

        listener.onSlashCommandInteraction(event);

        verify(player, never()).play(any());
        verify(event).reply("Command not recognized");
    }

    /**
     * Tests that non-guild interactions are ignored.
     * <p>
     * Given an interaction from outside a guild, when handled, then no player method is
     * invoked and no reply is sent.
     * </p>
     */
    @Test
    void onSlashCommandInteraction_notFromGuild_ignoresEvent() {
        when(event.isFromGuild()).thenReturn(false);

        listener.onSlashCommandInteraction(event);

        verify(player, never()).play(any());
        verify(event, never()).reply(anyString());
    }
}
