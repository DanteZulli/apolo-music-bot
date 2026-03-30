package com.zullid.apolo_music_bot.handlers;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame;
import java.nio.ByteBuffer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AudioPlayerSendHandlerTest {

    @Mock
    private AudioPlayer audioPlayer;

    @Mock
    private AudioFrame audioFrame;

    private AudioPlayerSendHandler handler;

    @BeforeEach
    void setUp() {
        handler = new AudioPlayerSendHandler(audioPlayer);
    }

    @Test
    void canProvide_whenFrameAvailable_returnsTrue() {
        when(audioPlayer.provide()).thenReturn(audioFrame);

        boolean result = handler.canProvide();

        assertTrue(result);
    }

    @Test
    void canProvide_whenNoFrameAvailable_returnsFalse() {
        when(audioPlayer.provide()).thenReturn(null);

        boolean result = handler.canProvide();

        assertFalse(result);
    }

    @Test
    void provide20MsAudio_returnsAudioData() {
        byte[] data = new byte[]{1, 2, 3, 4};
        when(audioPlayer.provide()).thenReturn(audioFrame);
        when(audioFrame.getData()).thenReturn(data);

        handler.canProvide();
        ByteBuffer result = handler.provide20MsAudio();

        assertNotNull(result);
        assertArrayEquals(data, result.array());
    }

    @Test
    void isOpus_returnsTrue() {
        assertTrue(handler.isOpus());
    }
}