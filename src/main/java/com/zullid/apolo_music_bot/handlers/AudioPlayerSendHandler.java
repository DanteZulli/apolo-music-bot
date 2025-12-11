package com.zullid.apolo_music_bot.handlers;

import java.nio.ByteBuffer;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame;

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

    @Override
    public boolean canProvide() {
        lastFrame = audioPlayer.provide();
        return lastFrame != null;
    }

    @Override
    public ByteBuffer provide20MsAudio() {
        return ByteBuffer.wrap(lastFrame.getData());
    }

    @Override
    public boolean isOpus() {
        return true;
    }
}
