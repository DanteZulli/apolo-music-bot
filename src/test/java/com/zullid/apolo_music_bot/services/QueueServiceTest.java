package com.zullid.apolo_music_bot.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for {@link QueueService}.
 * <p>
 * Verifies immediate playback versus queueing, skipping, clearing, snapshot copies and
 * size checks, using a mocked player service and player.
 * </p>
 *
 * @author Dante Zulli (dantezulli2004@gmail.com)
 */
@ExtendWith(MockitoExtension.class)
class QueueServiceTest {

    @Mock
    private AudioPlayerService audioPlayerService;

    @Mock
    private AudioPlayer audioPlayer;

    @Mock
    private AudioTrack currentlyPlayingTrack;

    private QueueService queueService;

    /**
     * Initializes the service with a mocked player service before each test.
     */
    @BeforeEach
    void setUp() {
        queueService = new QueueService(audioPlayerService);
    }

    /**
     * Creates a mocked track with the given title.
     *
     * @param title the track title
     * @return the mocked track
     */
    private AudioTrack createMockTrack(String title) {
        AudioTrack track = mock(AudioTrack.class);
        AudioTrackInfo info = new AudioTrackInfo(
            title,
            "test",
            0L,
            "id",
            false,
            null
        );
        lenient().doReturn(info).when(track).getInfo();
        return track;
    }

    /**
     * Tests that idle playback starts immediately.
     * <p>
     * Given no track playing, when a track is added, then it is played directly.
     * </p>
     */
    @Test
    void addToQueue_whenNoTrackPlaying_playsTrackImmediately() {
        AudioTrack track = createMockTrack("Test Song");
        when(audioPlayerService.getPlayer()).thenReturn(audioPlayer);
        when(audioPlayer.getPlayingTrack()).thenReturn(null);

        queueService.addToQueue(track);

        verify(audioPlayer).playTrack(track);
    }

    /**
     * Tests that busy playback queues the track.
     * <p>
     * Given a track playing, when another track is added, then it is queued without interrupting playback.
     * </p>
     */
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

    /**
     * Tests that skipping advances the queue.
     * <p>
     * Given queued tracks, when skip is requested, then the current track stops and the next one plays.
     * </p>
     */
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

    /**
     * Tests that clearing empties the queue and stops playback.
     * <p>
     * Given queued tracks, when clear is requested, then the queue is empty and the player stops.
     * </p>
     */
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

    /**
     * Tests that the queue snapshot is a defensive copy.
     * <p>
     * Given queued tracks, when the list is requested and mutated, then the internal queue is unaffected.
     * </p>
     */
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

    /**
     * Tests that an empty queue reports empty.
     * <p>
     * Given no tracks, when checked, then it returns true.
     * </p>
     */
    @Test
    void isQueueEmpty_whenEmpty_returnsTrue() {
        assertTrue(queueService.isQueueEmpty());
    }

    /**
     * Tests that a non-empty queue reports non-empty.
     * <p>
     * Given a queued track, when checked, then it returns false.
     * </p>
     */
    @Test
    void isQueueEmpty_whenNotEmpty_returnsFalse() {
        when(audioPlayerService.getPlayer()).thenReturn(audioPlayer);
        when(audioPlayer.getPlayingTrack()).thenReturn(currentlyPlayingTrack);

        queueService.addToQueue(createMockTrack("Song"));

        assertFalse(queueService.isQueueEmpty());
    }

    /**
     * Tests that the size reflects queued tracks.
     * <p>
     * Given two queued tracks, when sized, then it returns 2.
     * </p>
     */
    @Test
    void getQueueSize_returnsCorrectSize() {
        when(audioPlayerService.getPlayer()).thenReturn(audioPlayer);
        when(audioPlayer.getPlayingTrack()).thenReturn(currentlyPlayingTrack);

        queueService.addToQueue(createMockTrack("Song 1"));
        queueService.addToQueue(createMockTrack("Song 2"));

        assertEquals(2, queueService.getQueueSize());
    }

    /**
     * Tests that advancing an empty queue is a no-op.
     * <p>
     * Given an empty queue, when next is requested, then it stays empty.
     * </p>
     */
    @Test
    void playNextTrack_whenQueueEmpty_logsEmptyQueue() {
        when(audioPlayerService.getPlayer()).thenReturn(audioPlayer);

        queueService.playNextTrack();

        assertTrue(queueService.isQueueEmpty());
    }

    /**
     * Tests that advancing a non-empty queue plays next.
     * <p>
     * Given a queued track, when next is requested, then it plays and the queue drains.
     * </p>
     */
    @Test
    void playNextTrack_whenQueueHasTracks_playsNextTrack() {
        AudioTrack playingTrack = createMockTrack("Playing");
        AudioTrack queuedTrack = createMockTrack("Queued");

        when(audioPlayerService.getPlayer()).thenReturn(audioPlayer);
        when(audioPlayer.getPlayingTrack()).thenReturn(playingTrack);

        queueService.addToQueue(queuedTrack);

        queueService.playNextTrack();

        verify(audioPlayer).playTrack(queuedTrack);
        assertEquals(0, queueService.getQueueSize());
    }
}
