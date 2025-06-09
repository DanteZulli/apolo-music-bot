package com.zullid.apolo_music_bot.services;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.springframework.stereotype.Service;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class QueueService {
    
    private final AudioPlayerService audioPlayerService;
    
    @Getter
    private final BlockingQueue<AudioTrack> queue = new LinkedBlockingQueue<>();
    
    public void addToQueue(AudioTrack track) {
        if (audioPlayerService.getPlayer().getPlayingTrack() == null) {
            audioPlayerService.getPlayer().playTrack(track);
            log.info("Now playing: {}", track.getInfo().title);
        } else {
            queue.offer(track);
            log.info("Added to queue: {}", track.getInfo().title);
        }
    }
    
    public void skipCurrentTrack() {
        AudioPlayer player = audioPlayerService.getPlayer();
        player.stopTrack();
        playNextTrack();
    }
    
    public void playNextTrack() {
        AudioPlayer player = audioPlayerService.getPlayer();
        AudioTrack nextTrack = queue.poll();
        
        if (nextTrack != null) {
            player.playTrack(nextTrack);
            log.info("Now playing: {}", nextTrack.getInfo().title);
        } else {
            log.info("Queue is empty");
        }
    }
    
    public void clearQueue() {
        queue.clear();
        audioPlayerService.getPlayer().stopTrack();
        log.info("Queue cleared");
    }
    
    public List<AudioTrack> getQueueList() {
        return new ArrayList<>(queue);
    }
    
    public boolean isQueueEmpty() {
        return queue.isEmpty();
    }
    
    public int getQueueSize() {
        return queue.size();
    }
} 