package com.zullid.apolo_music_bot.player.state;

import com.zullid.apolo_music_bot.services.AudioPlayerService;
import com.zullid.apolo_music_bot.services.QueueService;
import com.zullid.apolo_music_bot.services.VoiceChannelService;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

/**
 * Common interface for all states.
 *
 * @author Dante Zulli (dantezulli2004@gmail.com)
 */
public abstract class PlayerState {
    protected AudioPlayerService player;

    protected PlayerState(AudioPlayerService player) {
        this.player = player;
    }

    public abstract void onPlay(QueueService queueService, VoiceChannelService voiceChannelService,
            SlashCommandInteractionEvent event);

    public abstract void onPause(QueueService queueService, VoiceChannelService voiceChannelService,
            SlashCommandInteractionEvent event);

    public abstract void onResume(QueueService queueService, VoiceChannelService voiceChannelService,
            SlashCommandInteractionEvent event);

    public abstract void onStop(QueueService queueService, VoiceChannelService voiceChannelService,
            SlashCommandInteractionEvent event);

    public abstract void onSkip(QueueService queueService, VoiceChannelService voiceChannelService,
            SlashCommandInteractionEvent event);
}
