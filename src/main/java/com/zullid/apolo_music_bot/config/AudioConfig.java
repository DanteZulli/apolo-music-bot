package com.zullid.apolo_music_bot.config;

import com.zullid.apolo_music_bot.handlers.AudioEventHandler;
import com.zullid.apolo_music_bot.services.AudioPlayerService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for initializing audio-related components.
 * <p>
 * This class sets up the audio player service and registers the audio event handler
 * to listen for audio events, enabling state management based on playback status.
 * </p>
 *
 * @see AudioPlayerService
 * @see AudioEventHandler
 * @author Dante Zulli (dantezulli2004@gmail.com)
 */
@Configuration
@RequiredArgsConstructor
public class AudioConfig {

    private final AudioPlayerService audioPlayerService;
    private final AudioEventHandler audioEventHandler;

    /**
     * Registers the audio event handler on the audio player.
     */
    @PostConstruct
    public void init() {
        audioPlayerService.addListener(audioEventHandler);
    }
}
