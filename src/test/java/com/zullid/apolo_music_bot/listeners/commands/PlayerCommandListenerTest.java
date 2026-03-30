package com.zullid.apolo_music_bot.listeners.commands;

import com.zullid.apolo_music_bot.player.Player;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlayerCommandListenerTest {

    @Mock
    private Player player;

    @Mock
    private SlashCommandInteractionEvent event;

    private PlayerCommandListener listener;

    @BeforeEach
    void setUp() {
        listener = new PlayerCommandListener(player);
    }

    @Test
    void onSlashCommandInteraction_playCommand_delegatesToPlayer() {
        when(event.isFromGuild()).thenReturn(true);
        when(event.getName()).thenReturn("play");

        listener.onSlashCommandInteraction(event);

        verify(player).play(event);
    }

    @Test
    void onSlashCommandInteraction_pauseCommand_delegatesToPlayer() {
        when(event.isFromGuild()).thenReturn(true);
        when(event.getName()).thenReturn("pause");

        listener.onSlashCommandInteraction(event);

        verify(player).pause(event);
    }

    @Test
    void onSlashCommandInteraction_resumeCommand_delegatesToPlayer() {
        when(event.isFromGuild()).thenReturn(true);
        when(event.getName()).thenReturn("resume");

        listener.onSlashCommandInteraction(event);

        verify(player).resume(event);
    }

    @Test
    void onSlashCommandInteraction_stopCommand_delegatesToPlayer() {
        when(event.isFromGuild()).thenReturn(true);
        when(event.getName()).thenReturn("stop");

        listener.onSlashCommandInteraction(event);

        verify(player).stop(event);
    }

    @Test
    void onSlashCommandInteraction_skipCommand_delegatesToPlayer() {
        when(event.isFromGuild()).thenReturn(true);
        when(event.getName()).thenReturn("skip");

        listener.onSlashCommandInteraction(event);

        verify(player).skip(event);
    }

    @Test
    void onSlashCommandInteraction_helpCommand_delegatesToPlayer() {
        when(event.isFromGuild()).thenReturn(true);
        when(event.getName()).thenReturn("help");

        listener.onSlashCommandInteraction(event);

        verify(player).help(event);
    }

    @Test
    void onSlashCommandInteraction_queueCommand_delegatesToPlayer() {
        when(event.isFromGuild()).thenReturn(true);
        when(event.getName()).thenReturn("queue");

        listener.onSlashCommandInteraction(event);

        verify(player).queue(event);
    }

    @Test
    void onSlashCommandInteraction_unknownCommand_repliesNotRecognized() {
        when(event.isFromGuild()).thenReturn(true);
        when(event.getName()).thenReturn("unknown");
        
        net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction replyAction = mock(net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction.class);
        when(event.reply(anyString())).thenReturn(replyAction);
        when(replyAction.setEphemeral(anyBoolean())).thenReturn(replyAction);
        
        listener.onSlashCommandInteraction(event);

        verify(player, never()).play(any());
        verify(event).reply("Command not recognized");
    }

    @Test
    void onSlashCommandInteraction_notFromGuild_ignoresEvent() {
        when(event.isFromGuild()).thenReturn(false);

        listener.onSlashCommandInteraction(event);

        verify(player, never()).play(any());
        verify(event, never()).reply(anyString());
    }
}