package com.zullid.apolo_music_bot.player;

import org.springframework.stereotype.Component;

import com.zullid.apolo_music_bot.player.state.PlayerState;
import com.zullid.apolo_music_bot.player.state.ReadyState;
import com.zullid.apolo_music_bot.services.AudioPlayerService;
import com.zullid.apolo_music_bot.services.QueueService;
import com.zullid.apolo_music_bot.services.VoiceChannelService;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

/**
 * The main player component managing music playback states.
 * <p>
 * This class implements the State pattern to handle different playback states
 * (Ready, Playing, Paused). It delegates command handling to the current state
 * and ensures the user is in a voice channel before executing commands.
 * </p>
 *
 * @author Dante Zulli (dantezulli2004@gmail.com)
 */
@Getter
@Setter
@Component
@RequiredArgsConstructor
public class Player {

    private final AudioPlayerService audioPlayerService;
    private final QueueService queueService;
    private final VoiceChannelService voiceChannelService;

    private PlayerState state;

    @PostConstruct
    public void init() {
        this.state = new ReadyState(this);
    }

    public void play(SlashCommandInteractionEvent event) {
        checkVoiceChannel(event);
        state.onPlay(event);
    }

    public void pause(SlashCommandInteractionEvent event) {
        checkVoiceChannel(event);
        state.onPause(event);
    }

    public void resume(SlashCommandInteractionEvent event) {
        checkVoiceChannel(event);
        state.onResume(event);
    }

    public void stop(SlashCommandInteractionEvent event) {
        checkVoiceChannel(event);
        state.onStop(event);
    }

    public void skip(SlashCommandInteractionEvent event) {
        checkVoiceChannel(event);
        state.onSkip(event);
    }

    private void checkVoiceChannel(SlashCommandInteractionEvent event) {
        if (!voiceChannelService.isConnected(event.getGuild())
                && !voiceChannelService.joinVoiceChannel(event.getMember())) {
            event.reply("You need to be in a voice channel first!").queue();
        }
    }

}
