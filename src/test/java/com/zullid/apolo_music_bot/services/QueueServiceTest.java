package com.zullid.apolo_music_bot.services;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QueueServiceTest {

    @Mock
    private AudioPlayerService audioPlayerService;

    @Mock
    private AudioPlayer audioPlayer;

    @Mock
    private AudioTrack currentlyPlayingTrack;

    private QueueService queueService;

    @BeforeEach
    void setUp() {
        queueService = new QueueService(audioPlayerService);
    }

    private AudioTrack createMockTrack(String title) {
        AudioTrack track = mock(AudioTrack.class);
        AudioTrackInfo info = new AudioTrackInfo(title, "test", 0L, "id", false, null);
        lenient().doReturn(info).when(track).getInfo();
        return track;
    }

    @Test
    void addToQueue_whenNoTrackPlaying_playsTrackImmediately() {
        AudioTrack track = createMockTrack("Test Song");
        when(audioPlayerService.getPlayer()).thenReturn(audioPlayer);
        when(audioPlayer.getPlayingTrack()).thenReturn(null);

        queueService.addToQueue(track);

        verify(audioPlayer).playTrack(track);
    }

    @Test
    void addToQueue_whenTrackIsPlaying_addsToQueue() {
        AudioTrack playingTrack = createMockTrack("Playing");
        AudioTrack queuedTrack = createMockTrack("Queued");

        lenient().when(audioPlayerService.getPlayer()).thenReturn(audioPlayer);
        lenient().when(audioPlayer.getPlayingTrack()).thenReturn(playingTrack);

        queueService.addToQueue(queuedTrack);

        assertEquals(1, queueService.getQueueSize());
        verify(audioPlayer, never()).playTrack(any(AudioTrack.class));
    }

    @Test
    void skipCurrentTrack_stopsAndPlaysNext() {
        when(audioPlayerService.getPlayer()).thenReturn(audioPlayer);
        when(audioPlayer.getPlayingTrack())
            .thenReturn(currentlyPlayingTrack)
            .thenReturn(currentlyPlayingTrack)
            .thenReturn(null);

        AudioTrack currentTrack = createMockTrack("Current");
        AudioTrack nextTrack = createMockTrack("Next");
        queueService.addToQueue(currentTrack);
        queueService.addToQueue(nextTrack);

        queueService.skipCurrentTrack();

        verify(audioPlayer).stopTrack();
        verify(audioPlayer).playTrack(any(AudioTrack.class));
    }

    @Test
    void clearQueue_removesAllTracksAndStopsPlayback() {
        when(audioPlayerService.getPlayer()).thenReturn(audioPlayer);
        when(audioPlayer.getPlayingTrack()).thenReturn(currentlyPlayingTrack);

        queueService.addToQueue(createMockTrack("Song 1"));
        queueService.addToQueue(createMockTrack("Song 2"));
        assertEquals(2, queueService.getQueueSize());

        queueService.clearQueue();

        assertTrue(queueService.isQueueEmpty());
        verify(audioPlayer).stopTrack();
    }

    @Test
    void getQueueList_returnsCopyOfQueue() {
        when(audioPlayerService.getPlayer()).thenReturn(audioPlayer);
        when(audioPlayer.getPlayingTrack()).thenReturn(currentlyPlayingTrack);

        queueService.addToQueue(createMockTrack("Song 1"));
        queueService.addToQueue(createMockTrack("Song 2"));

        List<AudioTrack> list = queueService.getQueueList();

        assertEquals(2, list.size());
        list.clear();
        assertEquals(2, queueService.getQueueSize());
    }

    @Test
    void isQueueEmpty_whenEmpty_returnsTrue() {
        assertTrue(queueService.isQueueEmpty());
    }

    @Test
    void isQueueEmpty_whenNotEmpty_returnsFalse() {
        when(audioPlayerService.getPlayer()).thenReturn(audioPlayer);
        when(audioPlayer.getPlayingTrack()).thenReturn(currentlyPlayingTrack);

        queueService.addToQueue(createMockTrack("Song"));

        assertFalse(queueService.isQueueEmpty());
    }

    @Test
    void getQueueSize_returnsCorrectSize() {
        when(audioPlayerService.getPlayer()).thenReturn(audioPlayer);
        when(audioPlayer.getPlayingTrack()).thenReturn(currentlyPlayingTrack);

        queueService.addToQueue(createMockTrack("Song 1"));
        queueService.addToQueue(createMockTrack("Song 2"));

        assertEquals(2, queueService.getQueueSize());
    }

    @Test
    void playNextTrack_whenQueueEmpty_logsEmptyQueue() {
        when(audioPlayerService.getPlayer()).thenReturn(audioPlayer);

        queueService.playNextTrack();

        assertTrue(queueService.isQueueEmpty());
    }
}