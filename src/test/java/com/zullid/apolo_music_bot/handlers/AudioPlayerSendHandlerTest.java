package com.zullid.apolo_music_bot.handlers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame;
import java.nio.ByteBuffer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for {@link AudioPlayerSendHandler}.
 * <p>
 * Verifies the JDA audio bridge: frame availability, 20ms Opus provision and format
 * flag, using a mocked LavaPlayer and audio frame.
 * </p>
 *
 * @author Dante Zulli (dantezulli2004@gmail.com)
 */
@ExtendWith(MockitoExtension.class)
class AudioPlayerSendHandlerTest {

    @Mock
    private AudioPlayer audioPlayer;

    @Mock
    private AudioFrame audioFrame;

    private AudioPlayerSendHandler handler;

    /**
     * Initializes the handler with a mocked audio player before each test.
     */
    @BeforeEach
    void setUp() {
        handler = new AudioPlayerSendHandler(audioPlayer);
    }

    /**
     * Tests that available frames are reported as providable.
     * <p>
     * Given the player returns a frame, when {@code canProvide} is called, then it returns
     * {@code true}.
     * </p>
     */
    @Test
    void canProvide_whenFrameAvailable_returnsTrue() {
        when(audioPlayer.provide()).thenReturn(audioFrame);

        boolean result = handler.canProvide();

        assertTrue(result);
    }

    /**
     * Tests that missing frames are reported as not providable.
     * <p>
     * Given the player returns {@code null}, when {@code canProvide} is called, then it
     * returns {@code false}.
     * </p>
     */
    @Test
    void canProvide_whenNoFrameAvailable_returnsFalse() {
        when(audioPlayer.provide()).thenReturn(null);

        boolean result = handler.canProvide();

        assertFalse(result);
    }

    /**
     * Tests that the cached frame data is exposed as a byte buffer.
     * <p>
     * Given a frame with raw bytes, when it is cached via {@code canProvide} and
     * {@code provide20MsAudio} is called, then the same bytes are returned.
     * </p>
     */
    @Test
    void provide20MsAudio_returnsAudioData() {
        byte[] data = new byte[] { 1, 2, 3, 4 };
        when(audioPlayer.provide()).thenReturn(audioFrame);
        when(audioFrame.getData()).thenReturn(data);

        handler.canProvide();
        ByteBuffer result = handler.provide20MsAudio();

        assertNotNull(result);
        assertArrayEquals(data, result.array());
    }

    /**
     * Tests that frames are flagged as Opus so JDA skips transcoding.
     * <p>
     * Given any handler state, when {@code isOpus} is called, then it returns
     * {@code true}.
     * </p>
     */
    @Test
    void isOpus_returnsTrue() {
        assertTrue(handler.isOpus());
    }
}
