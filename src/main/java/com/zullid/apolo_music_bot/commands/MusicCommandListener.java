package com.zullid.apolo_music_bot.commands;

import org.springframework.stereotype.Component;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class MusicCommandListener extends ListenerAdapter {
    
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
        // TODO: Implementar lógica para unirse al canal de voz
        log.debug("Join command received");
        event.reply("Join command in development").queue();
    }

    private void handleLeaveCommand(SlashCommandInteractionEvent event) {
        // TODO: Implementar lógica para salir del canal de voz
        log.debug("Leave command received");
        event.reply("Leave command in development").queue();
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