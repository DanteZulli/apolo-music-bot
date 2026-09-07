package com.zullid.apolo_music_bot.handlers;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame;
import java.nio.ByteBuffer;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.audio.AudioSendHandler;

/**
 * Audio send handler that provides audio frames from the Lavaplayer AudioPlayer to JDA.
 * <p>
 * This class implements {@link net.dv8tion.jda.api.audio.AudioSendHandler} to bridge
 * the Lavaplayer audio output with Discord's voice channel audio sending. It provides
 * 20ms audio frames in Opus format when requested by JDA.
 * </p>
 *
 * @author Dante Zulli (dantezulli2004@gmail.com)
 */
@RequiredArgsConstructor
public class AudioPlayerSendHandler implements AudioSendHandler {

    private final AudioPlayer audioPlayer;
    private AudioFrame lastFrame;

    /**
     * Caches the latest frame and reports whether audio is available.
     *
     * @return {@code true} when a frame could be provided, {@code false} otherwise
     */
    @Override
    public boolean canProvide() {
        lastFrame = audioPlayer.provide();
        return lastFrame != null;
    }

    /**
     * Provides 20ms of cached Opus audio to JDA.
     *
     * @return a buffer wrapping the last frame data
     */
    @Override
    public ByteBuffer provide20MsAudio() {
        return ByteBuffer.wrap(lastFrame.getData());
    }

    /**
     * Indicates that provided audio is already Opus encoded.
     *
     * @return {@code true} always, frames require no transcoding
     */
    @Override
    public boolean isOpus() {
        return true;
    }
}
