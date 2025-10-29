package com.zullid.apolo_music_bot.config;

import org.springframework.context.annotation.Configuration;

import com.zullid.apolo_music_bot.handlers.AudioEventHandler;
import com.zullid.apolo_music_bot.services.AudioPlayerService;
import com.zullid.apolo_music_bot.services.QueueService;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
/**
 * Configuration class for initializing audio-related components.
 * 
 * @see AudioPlayerService
 * @see QueueService
 * @author Dante Zulli (dantezulli2004@gmail.com)
 */
public class AudioConfig {
    
    private final AudioPlayerService audioPlayerService;
    private final QueueService queueService;
    
    @PostConstruct
    public void init() {
        AudioEventHandler eventHandler = new AudioEventHandler(queueService);
        audioPlayerService.addListener(eventHandler);
    }
} 