package com.zullid.apolo_music_bot.player.state;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.zullid.apolo_music_bot.player.Player;
import com.zullid.apolo_music_bot.services.AudioPlayerService;
import com.zullid.apolo_music_bot.services.QueueService;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class PausedState extends PlayerState {

    private final AudioPlayerService audioPlayerService;
    private final QueueService queueService;

    public PausedState(Player player) {
        super(player);
        this.audioPlayerService = player.getAudioPlayerService();
        this.queueService = player.getQueueService();
    }

    @Override
    public void onPlay(SlashCommandInteractionEvent event) {
        event.reply("Cannot play while paused. Use resume.").queue();
    }

    @Override
    public void onPause(SlashCommandInteractionEvent event) {
        event.reply("Player is already paused!").queue();
    }

    @Override
    public void onResume(SlashCommandInteractionEvent event) {
        // For the moment, we ensure here that something is playing, because we don't
        // have an empty queue listener yet.
        AudioPlayer audioPlayer = audioPlayerService.getPlayer();
        if (audioPlayer.getPlayingTrack() == null) {
            event.reply("Nothing is playing!").queue();
            player.setState(new ReadyState(player));
            return;
        }
        audioPlayer.setPaused(false);
        event.reply("Resumed the current track").queue();
        player.setState(new PlayingState(player));
    }

    @Override
    public void onStop(SlashCommandInteractionEvent event) {
        queueService.clearQueue();
        audioPlayerService.getPlayer().stopTrack();
        event.reply("Stopped playback and cleared the queue").queue();
        player.setState(new ReadyState(player));
    }

    @Override
    public void onSkip(SlashCommandInteractionEvent event) {
        event.reply("Cannot skip while paused. Use resume.").queue();
    }
}
