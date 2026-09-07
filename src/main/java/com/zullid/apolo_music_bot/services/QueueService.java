package com.zullid.apolo_music_bot.services;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service responsible for managing the music queue.
 * <p>
 * This service handles adding tracks to the queue, playing the next track,
 * skipping tracks, clearing the queue, and checking queue status. It uses a
 * bounded {@link BlockingQueue} holding at most {@code MAX_QUEUE_SIZE} tracks,
 * matching the number of queued tracks shown by the {@code /queue} command.
 * When nothing is playing, {@code addToQueue} starts playback immediately
 * instead of queueing.
 * </p>
 *
 * @see AudioPlayerService
 * @author Dante Zulli (dantezulli2004@gmail.com)
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class QueueService {

    /** Maximum number of tracks held in the queue (excluding the playing track). */
    public static final int MAX_QUEUE_SIZE = 50;

    private final AudioPlayerService audioPlayerService;

    @Getter
    private final BlockingQueue<AudioTrack> queue = new LinkedBlockingQueue<>(
        MAX_QUEUE_SIZE
    );

    /**
     * Adds a track to the queue or plays it immediately when idle.
     * <p>
     * When the queue already holds {@code MAX_QUEUE_SIZE} tracks the track is
     * rejected.
     * </p>
     *
     * @param track the track to add or play
     * @return {@code true} when the track will be played, {@code false} when rejected (queue full)
     */
    public boolean addToQueue(AudioTrack track) {
        if (audioPlayerService.getPlayer().getPlayingTrack() == null) {
            audioPlayerService.getPlayer().playTrack(track);
            log.debug(
                "Playing immediately (queue was idle): {}",
                track.getInfo().title
            );
            return true;
        }
        if (queue.offer(track)) {
            log.debug("Added to queue: {}", track.getInfo().title);
            return true;
        }
        log.warn(
            "Queue is full (max {}), rejecting: {}",
            MAX_QUEUE_SIZE,
            track.getInfo().title
        );
        return false;
    }

    /**
     * Stops the current track and starts the next queued track, if any.
     */
    public void skipCurrentTrack() {
        AudioPlayer player = audioPlayerService.getPlayer();
        player.stopTrack();
        playNextTrack();
    }

    /**
     * Polls the queue and plays the next track.
     * <p>
     * Does nothing besides logging when the queue is empty.
     * </p>
     */
    public void playNextTrack() {
        AudioPlayer player = audioPlayerService.getPlayer();
        AudioTrack nextTrack = queue.poll();

        if (nextTrack != null) {
            player.playTrack(nextTrack);
            log.debug("Advanced to next track: {}", nextTrack.getInfo().title);
        } else {
            log.debug("Queue is empty, nothing to play");
        }
    }

    /**
     * Clears the queue and stops the current track.
     */
    public void clearQueue() {
        queue.clear();
        audioPlayerService.getPlayer().stopTrack();
        log.info("Queue cleared");
    }

    /**
     * Returns a snapshot copy of the current queue.
     *
     * @return a new list with the queued tracks in order
     */
    public List<AudioTrack> getQueueList() {
        return new ArrayList<>(queue);
    }

    /**
     * Checks whether the queue is empty.
     *
     * @return {@code true} when no tracks are queued, {@code false} otherwise
     */
    public boolean isQueueEmpty() {
        return queue.isEmpty();
    }

    /**
     * Returns the number of queued tracks.
     *
     * @return the queue size
     */
    public int getQueueSize() {
        return queue.size();
    }
}
