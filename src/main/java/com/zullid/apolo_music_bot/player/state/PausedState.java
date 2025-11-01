package com.zullid.apolo_music_bot.player.state;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.zullid.apolo_music_bot.player.Player;
import com.zullid.apolo_music_bot.services.AudioPlayerService;
import com.zullid.apolo_music_bot.services.QueueService;
import com.zullid.apolo_music_bot.services.VoiceChannelService;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class PausedState extends PlayerState {
    private final Player playerContext;

    public PausedState(AudioPlayerService player, Player playerContext) {
        super(player);
        this.playerContext = playerContext;
    }

    @Override
    public void onPlay(QueueService queueService, VoiceChannelService voiceChannelService, SlashCommandInteractionEvent event) {
        event.reply("Cannot play while paused. Use resume.").queue();
    }

    @Override
    public void onPause(QueueService queueService, VoiceChannelService voiceChannelService, SlashCommandInteractionEvent event) {
        event.reply("Player is already paused!").queue();
    }

    @Override
    public void onResume(QueueService queueService, VoiceChannelService voiceChannelService, SlashCommandInteractionEvent event) {
        AudioPlayer audioPlayer = player.getPlayer();
        if (audioPlayer.getPlayingTrack() == null) {
            event.reply("Nothing is playing!").queue();
            return;
        }
        if (!audioPlayer.isPaused()) {
            event.reply("Player is not paused!").queue();
            return;
        }
        audioPlayer.setPaused(false);
        event.reply("Resumed the current track").queue();
        playerContext.setState(new PlayingState(player, playerContext));
    }

    @Override
    public void onStop(QueueService queueService, VoiceChannelService voiceChannelService, SlashCommandInteractionEvent event) {
        queueService.clearQueue();
        player.getPlayer().stopTrack();
        event.reply("Stopped playback and cleared the queue").queue();
        playerContext.setState(new ReadyState(player, playerContext));
    }

    @Override
    public void onSkip(QueueService queueService, VoiceChannelService voiceChannelService, SlashCommandInteractionEvent event) {
        queueService.skipCurrentTrack();
        event.reply("Skipped to the next track").queue();
    }
}
