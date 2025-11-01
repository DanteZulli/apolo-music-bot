package com.zullid.apolo_music_bot.listeners.events;

import org.springframework.stereotype.Component;

import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
/**
 * Listener for the Ready event when the bot is fully connected and ready.
 * 
 * @author Dante Zulli (dantezulli2004@gmail.com)
 */
public class ReadyListener extends ListenerAdapter {

    @Override
    public void onReady(ReadyEvent event) {
        log.info("Apolo is ready! Connected as {}", event.getJDA().getSelfUser().getAsTag());
    }
} 