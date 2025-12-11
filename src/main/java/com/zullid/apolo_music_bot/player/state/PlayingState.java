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

import lombok.extern.slf4j.Slf4j;

/**
 * State representing the player is actively playing music.
 * <p>
 * In this state, the player can add tracks to the queue, pause playback,
 * stop and clear the queue, or skip to the next track. Resume is not allowed
 * since the player is already playing.
 * </p>
 *
 * @author Dante Zulli (dantezulli2004@gmail.com)
 */
@Slf4j
public class PlayingState extends PlayerState {

    private final AudioPlayerService audioPlayerService;
    private final QueueService queueService;

    PlayingState(Player player) {
        super(player);
        this.audioPlayerService = player.getAudioPlayerService();
        this.queueService = player.getQueueService();
    }

    @Override
    public void onPlay(SlashCommandInteractionEvent event) {
        String query = event.getOption("query").getAsString();
        event.deferReply().queue();

        audioPlayerService.getPlayerManager().loadItem(query, new AudioLoadResultHandler() {
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
                player.setState(new ReadyState(player));
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                event.getHook().sendMessage("Error loading track: " + exception.getMessage()).queue();
                log.error("Error loading track", exception);
                player.setState(new ReadyState(player));
            }
        });
    }

    @Override
    public void onPause(SlashCommandInteractionEvent event) {
        AudioPlayer audioPlayer = audioPlayerService.getPlayer();
        audioPlayer.setPaused(true);
        event.reply("Paused the current track").queue();
        player.setState(new PausedState(player));
    }

    @Override
    public void onResume(SlashCommandInteractionEvent event) {
        event.reply("Cannot resume: player is already playing.").queue();
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
        queueService.skipCurrentTrack();
        event.reply("Skipped to the next track").queue();
    }
}