package com.zullid.apolo_music_bot.listeners.events;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ReadyListenerTest {

    @Mock
    private ReadyEvent event;

    @Mock
    private JDA jda;

    @Mock
    private SelfUser selfUser;

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