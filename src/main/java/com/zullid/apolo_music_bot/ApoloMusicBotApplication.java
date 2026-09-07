package com.zullid.apolo_music_bot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point of the Apolo Music Bot Spring Boot application.
 *
 * @author Dante Zulli (dantezulli2004@gmail.com)
 */
@SpringBootApplication
public class ApoloMusicBotApplication {

    /**
     * Boots the Spring application context.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(ApoloMusicBotApplication.class, args);
    }
}
