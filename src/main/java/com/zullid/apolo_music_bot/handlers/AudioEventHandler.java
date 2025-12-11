package com.zullid.apolo_music_bot.handlers;

import org.springframework.stereotype.Component;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import com.zullid.apolo_music_bot.player.Player;
import com.zullid.apolo_music_bot.player.state.ReadyState;
import com.zullid.apolo_music_bot.services.AudioPlayerService;
import com.zullid.apolo_music_bot.services.QueueService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Handler for audio player events.
 * <p>
 * This class extends {@link com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter}
 * to listen for audio events such as track start, end, pause, and resume.
 * </p>
 *
 * @see QueueService
 * @see AudioPlayerService
 * @see Player
 * @author Dante Zulli (dantezulli2004@gmail.com)
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AudioEventHandler extends AudioEventAdapter {

    private final QueueService queueService;
    private final AudioPlayerService audioPlayerService;
    private final Player player;

    @Override
    public void onTrackEnd(AudioPlayer audioPlayer, AudioTrack track, AudioTrackEndReason endReason) {
        if (endReason.mayStartNext) {
            queueService.playNextTrack();
            if (queueService.isQueueEmpty() && audioPlayerService.getPlayer().getPlayingTrack() == null) {
                this.player.setState(new ReadyState(this.player));
            }
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