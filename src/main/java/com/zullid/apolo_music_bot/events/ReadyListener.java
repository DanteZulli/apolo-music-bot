package com.zullid.apolo_music_bot.events;

import org.springframework.stereotype.Component;

import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ReadyListener extends ListenerAdapter {

    @Override
    public void onReady(ReadyEvent event) {
        log.info("Apolo is ready! Connected as {}", event.getJDA().getSelfUser().getAsTag());
    }
} 