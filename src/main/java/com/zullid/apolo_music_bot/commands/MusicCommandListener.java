package com.zullid.apolo_music_bot.commands;

import org.springframework.stereotype.Component;

import com.zullid.apolo_music_bot.services.VoiceChannelService;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class MusicCommandListener extends ListenerAdapter {
    
    private final VoiceChannelService voiceChannelService;
    
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (!event.isFromGuild()) return;

        switch (event.getName()) {
            case "join":
                handleJoinCommand(event);
                break;
            case "leave":
                handleLeaveCommand(event);
                break;
            case "play":
                handlePlayCommand(event);
                break;
            case "pause":
                handlePauseCommand(event);
                break;
            case "resume":
                handleResumeCommand(event);
                break;
            case "stop":
                handleStopCommand(event);
                break;
            case "skip":
                handleSkipCommand(event);
                break;
            case "queue":
                handleQueueCommand(event);
                break;
            case "help":
                handleHelpCommand(event);
                break;
            default:
                event.reply("Command not recognized").setEphemeral(true).queue();
        }
    }

    private void handleJoinCommand(SlashCommandInteractionEvent event) {
        if (voiceChannelService.isConnected(event.getGuild())) {
            event.reply("I'm already in a voice channel!").queue();
            return;
        }

        if (voiceChannelService.joinVoiceChannel(event.getMember())) {
            event.reply("Joined your voice channel!").queue();
        } else {
            event.reply("You need to be in a voice channel first!").queue();
        }
    }

    private void handleLeaveCommand(SlashCommandInteractionEvent event) {
        if (!voiceChannelService.isConnected(event.getGuild())) {
            event.reply("I'm not in a voice channel!").queue();
            return;
        }

        voiceChannelService.leaveVoiceChannel(event.getGuild());
        event.reply("Left the voice channel!").queue();
    }

    private void handlePlayCommand(SlashCommandInteractionEvent event) {
        // TODO: Implementar lógica de reproducción
        log.debug("Play command received");
        event.reply("Play command in development").queue();
    }

    private void handlePauseCommand(SlashCommandInteractionEvent event) {
        // TODO: Implementar lógica para pausar la reproducción
        log.debug("Pause command received");
        event.reply("Pause command in development").queue();
    }

    private void handleResumeCommand(SlashCommandInteractionEvent event) {
        // TODO: Implementar lógica para reanudar la reproducción
        log.debug("Resume command received");
        event.reply("Resume command in development").queue();
    }

    private void handleStopCommand(SlashCommandInteractionEvent event) {
        // TODO: Implementar lógica para detener la reproducción y limpiar la cola
        log.debug("Stop command received");
        event.reply("Stop command in development").queue();
    }

    private void handleSkipCommand(SlashCommandInteractionEvent event) {
        // TODO: Implementar lógica para saltar a la siguiente canción
        log.debug("Skip command received");
        event.reply("Skip command in development").queue();
    }

    private void handleQueueCommand(SlashCommandInteractionEvent event) {
        // TODO: Implementar lógica para mostrar la cola de reproducción
        log.debug("Queue command received");
        event.reply("Queue command in development").queue();
    }

    private void handleHelpCommand(SlashCommandInteractionEvent event) {
        // TODO: Implementar lógica para mostrar la ayuda de comandos
        log.debug("Help command received");
        event.reply("Help command in development").queue();
    }
} 