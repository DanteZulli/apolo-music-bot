package com.zullid.apolo_music_bot.services;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import com.zullid.apolo_music_bot.player.Player;

@Slf4j
public class ReadyState extends State {
    private final Player playerContext;

    public ReadyState(AudioPlayerService player, Player playerContext) {
        super(player);
        this.playerContext = playerContext;
    }

    @Override
    public void onPlay(QueueService queueService, VoiceChannelService voiceChannelService,
            SlashCommandInteractionEvent event) {

        String query = event.getOption("query").getAsString();
        event.deferReply().queue();

        player.getPlayerManager().loadItem(query, new AudioLoadResultHandler() {

            @Override
            public void trackLoaded(AudioTrack track) {
                queueService.addToQueue(track);
                event.getHook().sendMessage("Added to queue: " + track.getInfo().title).queue();
                playerContext.setState(new PlayingState(player, playerContext));
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
                playerContext.setState(new PlayingState(player, playerContext));
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
    public void onPause(QueueService queueService, VoiceChannelService voiceChannelService,
            SlashCommandInteractionEvent event) {
        event.reply("Cannot pause: nothing is playing.").queue();
    }

    @Override
    public void onResume(QueueService queueService, VoiceChannelService voiceChannelService,
            SlashCommandInteractionEvent event) {
        event.reply("Cannot resume: nothing is playing.").queue();
    }

    @Override
    public void onStop(QueueService queueService, VoiceChannelService voiceChannelService,
            SlashCommandInteractionEvent event) {
        event.reply("Already stopped.").queue();
    }

    @Override
    public void onSkip(QueueService queueService, VoiceChannelService voiceChannelService,
            SlashCommandInteractionEvent event) {
        event.reply("Cannot skip: nothing is playing.").queue();
    }
}
