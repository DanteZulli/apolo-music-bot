package com.zullid.apolo_music_bot.commands;

import java.util.List;

import org.springframework.stereotype.Component;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import com.zullid.apolo_music_bot.services.AudioPlayerService;
import com.zullid.apolo_music_bot.services.QueueService;
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
    private final QueueService queueService;
    private final AudioPlayerService audioPlayerService;
    
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
        if (!voiceChannelService.isConnected(event.getGuild())) {
            if (!voiceChannelService.joinVoiceChannel(event.getMember())) {
                event.reply("You need to be in a voice channel first!").queue();
                return;
            }
        }

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
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                event.getHook().sendMessage("Error loading track: " + exception.getMessage()).queue();
                log.error("Error loading track", exception);
            }
        });
    }

    private void handlePauseCommand(SlashCommandInteractionEvent event) {
        if (!voiceChannelService.isConnected(event.getGuild())) {
            event.reply("I'm not in a voice channel!").queue();
            return;
        }

        AudioPlayer player = audioPlayerService.getPlayer();
        if (player.getPlayingTrack() == null) {
            event.reply("Nothing is playing!").queue();
            return;
        }

        if (player.isPaused()) {
            event.reply("Player is already paused!").queue();
            return;
        }

        player.setPaused(true);
        event.reply("Paused the current track").queue();
    }

    private void handleResumeCommand(SlashCommandInteractionEvent event) {
        if (!voiceChannelService.isConnected(event.getGuild())) {
            event.reply("I'm not in a voice channel!").queue();
            return;
        }

        AudioPlayer player = audioPlayerService.getPlayer();
        if (player.getPlayingTrack() == null) {
            event.reply("Nothing is playing!").queue();
            return;
        }

        if (!player.isPaused()) {
            event.reply("Player is not paused!").queue();
            return;
        }

        player.setPaused(false);
        event.reply("Resumed the current track").queue();
    }

    private void handleStopCommand(SlashCommandInteractionEvent event) {
        if (!voiceChannelService.isConnected(event.getGuild())) {
            event.reply("I'm not in a voice channel!").queue();
            return;
        }

        queueService.clearQueue();
        event.reply("Stopped playback and cleared the queue").queue();
    }

    private void handleSkipCommand(SlashCommandInteractionEvent event) {
        if (!voiceChannelService.isConnected(event.getGuild())) {
            event.reply("I'm not in a voice channel!").queue();
            return;
        }

        AudioPlayer player = audioPlayerService.getPlayer();
        if (player.getPlayingTrack() == null) {
            event.reply("Nothing is playing!").queue();
            return;
        }

        queueService.skipCurrentTrack();
        event.reply("Skipped to the next track").queue();
    }

    private void handleQueueCommand(SlashCommandInteractionEvent event) {
        if (!voiceChannelService.isConnected(event.getGuild())) {
            event.reply("I'm not in a voice channel!").queue();
            return;
        }

        AudioPlayer player = audioPlayerService.getPlayer();
        if (player.getPlayingTrack() == null && queueService.isQueueEmpty()) {
            event.reply("The queue is empty!").queue();
            return;
        }

        StringBuilder queueMessage = new StringBuilder();
        
        if (player.getPlayingTrack() != null) {
            queueMessage.append("**Now Playing:**\n");
            queueMessage.append(player.getPlayingTrack().getInfo().title).append("\n\n");
        }

        if (!queueService.isQueueEmpty()) {
            List<AudioTrack> queueList = queueService.getQueueList();
            int totalTracks = queueList.size();

            queueMessage.append("\n**Total tracks in queue:** ").append(totalTracks).append("\n");
            queueMessage.append("**Queue:**\n");
            int index = 1;
            int maxLength = 1500;
            
            for (AudioTrack track : queueList) {
                String trackEntry = index + ". " + track.getInfo().title + "\n";
                
                if (queueMessage.length() + trackEntry.length() > maxLength) {
                    break;
                }
                
                queueMessage.append(trackEntry);
                index++;
            }
            
        }

        event.reply(queueMessage.toString()).queue();
    }

    private void handleHelpCommand(SlashCommandInteractionEvent event) {
        StringBuilder helpMessage = new StringBuilder();
        helpMessage.append("**Available Commands:**\n\n");
        helpMessage.append("`/join` - Connect to your voice channel\n");
        helpMessage.append("`/leave` - Disconnect from voice channel\n");
        helpMessage.append("`/play <query>` - Play a song or add it to queue\n");
        helpMessage.append("`/pause` - Pause the current track\n");
        helpMessage.append("`/resume` - Resume the paused track\n");
        helpMessage.append("`/stop` - Stop playback and clear queue\n");
        helpMessage.append("`/skip` - Skip to the next track\n");
        helpMessage.append("`/queue` - Show the current queue\n");
        helpMessage.append("`/help` - Show this help message");

        event.reply(helpMessage.toString()).queue();
    }
} 