package com.zullid.apolo_music_bot.player.state;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.zullid.apolo_music_bot.player.Player;
import com.zullid.apolo_music_bot.services.AudioPlayerService;
import com.zullid.apolo_music_bot.services.QueueService;
import com.zullid.apolo_music_bot.services.VoiceChannelService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PlayingState extends PlayerState {
    private final Player playerContext;

    public PlayingState(AudioPlayerService player, Player playerContext) {
        super(player);
        this.playerContext = playerContext;
    }

    @Override
    public void onPlay(QueueService queueService, VoiceChannelService voiceChannelService, SlashCommandInteractionEvent event) {
        String query = event.getOption("query").getAsString();
        event.deferReply().queue();

        player.getPlayerManager().loadItem(query, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                queueService.addToQueue(track);
                event.getHook().sendMessage("Added to queue: " + track.getInfo().title).queue();
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                if (playlist.isSearchResult()) {
                    AudioTrack track = playlist.getTracks().get(0);
                    queueService.addToQueue(track);
                    event.getHook().sendMessage("Added to queue: " + track.getInfo().title).queue();
                } else {
                    for (AudioTrack track : playlist.getTracks()) {
                        queueService.addToQueue(track);
                    }
                    event.getHook().sendMessage("Added " + playlist.getTracks().size() + " tracks to queue").queue();
                }
            }

            @Override
            public void noMatches() {
                event.getHook().sendMessage("No matches found for: " + query).queue();
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                event.getHook().sendMessage("Error loading track: " + exception.getMessage()).queue();
                log.error("Error loading track", exception);
            }
        });
    }

    @Override
    public void onPause(QueueService queueService, VoiceChannelService voiceChannelService, SlashCommandInteractionEvent event) {
        AudioPlayer audioPlayer = player.getPlayer();
        if (audioPlayer.getPlayingTrack() == null) {
            event.reply("Nothing is playing!").queue();
            return;
        }
        audioPlayer.setPaused(true);
        event.reply("Paused the current track").queue();
        playerContext.setState(new PausedState(player, playerContext));
    }

    @Override
    public void onResume(QueueService queueService, VoiceChannelService voiceChannelService, SlashCommandInteractionEvent event) {
        event.reply("Cannot resume: player is already playing.").queue();
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