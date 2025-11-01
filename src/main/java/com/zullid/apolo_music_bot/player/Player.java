package com.zullid.apolo_music_bot.player;

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

@Getter
@Setter
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
        state.onPlay(event);
    }

    public void pause(SlashCommandInteractionEvent event) {
        state.onPause(event);
    }

    public void resume(SlashCommandInteractionEvent event) {
        state.onResume(event);
    }

    public void stop(SlashCommandInteractionEvent event) {
        state.onStop(event);
    }

    public void skip(SlashCommandInteractionEvent event) {
        state.onSkip(event);
    }

}
