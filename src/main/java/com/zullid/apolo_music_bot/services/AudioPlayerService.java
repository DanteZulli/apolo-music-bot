package com.zullid.apolo_music_bot.services;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventListener;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import dev.lavalink.youtube.YoutubeAudioSourceManager;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service responsible for managing the audio player using Lavaplayer.
 * <p>
 * This service initializes the AudioPlayerManager and AudioPlayer, registers
 * audio source managers for YouTube and remote sources, and provides methods
 * to add event listeners to the player.
 * </p>
 *
 * @author Dante Zulli (dantezulli2004@gmail.com)
 */
@Slf4j
@Service
public class AudioPlayerService {

    @Getter
    private AudioPlayerManager playerManager;

    @Getter
    private AudioPlayer player;

    /**
     * Initializes the player manager, registers YouTube and remote sources, and creates the player.
     */
    @PostConstruct
    public void init() {
        playerManager = new DefaultAudioPlayerManager();

        playerManager.registerSourceManager(new YoutubeAudioSourceManager());
        AudioSourceManagers.registerRemoteSources(
            playerManager,
            YoutubeAudioSourceManager.class
        );

        player = playerManager.createPlayer();

        log.info("AudioPlayer initialized successfully");
    }

    /**
     * Registers an event listener on the audio player.
     *
     * @param listener the listener to register
     */
    public void addListener(AudioEventListener listener) {
        player.addListener(listener);
    }
}
