package com.zullid.apolo_music_bot.handlers;

import org.springframework.stereotype.Component;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import com.zullid.apolo_music_bot.services.QueueService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
/**
 * Handler for audio player events.
 * 
 * @see QueueService
 * @author Dante Zulli (dantezulli2004@gmail.com)
 */
public class AudioEventHandler extends AudioEventAdapter {
    
    private final QueueService queueService;
    
    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if (endReason.mayStartNext) {
            queueService.playNextTrack();
        }
    }
    
    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {
        log.info("Started playing: {}", track.getInfo().title);
    }
    
    @Override
    public void onPlayerPause(AudioPlayer player) {
        log.info("Player paused");
    }
    
    @Override
    public void onPlayerResume(AudioPlayer player) {
        log.info("Player resumed");
    }
} 