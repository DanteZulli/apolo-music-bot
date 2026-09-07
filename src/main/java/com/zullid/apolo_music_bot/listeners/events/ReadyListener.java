package com.zullid.apolo_music_bot.listeners.events;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Component;

/**
 * Listener for the Ready event when the bot is fully connected and ready.
 * <p>
 * This class extends {@link net.dv8tion.jda.api.hooks.ListenerAdapter} to listen
 * for the {@link net.dv8tion.jda.api.events.session.ReadyEvent}, logging when the bot
 * has successfully connected to Discord and is ready to receive events.
 * </p>
 *
 * @author Dante Zulli (dantezulli2004@gmail.com)
 */
@Slf4j
@Component
public class ReadyListener extends ListenerAdapter {

    /**
     * Logs the bot tag once JDA is fully connected and ready.
     *
     * @param event the JDA ready event
     */
    @Override
    public void onReady(ReadyEvent event) {
        log.info(
            "Apolo is ready! Connected as {}",
            event.getJDA().getSelfUser().getAsTag()
        );
    }
}
