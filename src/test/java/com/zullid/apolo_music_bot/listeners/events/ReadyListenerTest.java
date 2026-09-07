package com.zullid.apolo_music_bot.listeners.events;

import static org.mockito.Mockito.*;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for {@link ReadyListener}.
 * <p>
 * Verifies that the ready event accesses the bot identity for logging, using mocked
 * JDA, self user and ready event without network access.
 * </p>
 *
 * @author Dante Zulli (dantezulli2004@gmail.com)
 */
@ExtendWith(MockitoExtension.class)
class ReadyListenerTest {

    @Mock
    private ReadyEvent event;

    @Mock
    private JDA jda;

    @Mock
    private SelfUser selfUser;

    /**
     * Tests that ready handling resolves the bot tag.
     * <p>
     * Given a ready event with mocked JDA identity, when {@code onReady} runs, then the
     * bot tag is looked up for logging.
     * </p>
     */
    @Test
    void onReady_logsReadyMessage() {
        when(event.getJDA()).thenReturn(jda);
        when(jda.getSelfUser()).thenReturn(selfUser);
        when(selfUser.getAsTag()).thenReturn("ApoloBot#1234");

        ReadyListener listener = new ReadyListener();
        listener.onReady(event);

        verify(event.getJDA()).getSelfUser();
        verify(selfUser).getAsTag();
    }
}
