package com.zullid.apolo_music_bot.services;

import org.springframework.stereotype.Service;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AudioPlayerService {
    
    @Getter
    private AudioPlayerManager playerManager;
    
    @Getter
    private AudioPlayer player;
    
    @PostConstruct
    public void init() {
        playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(playerManager);
        player = playerManager.createPlayer();
        
        log.info("AudioPlayer initialized successfully");
    }
} 