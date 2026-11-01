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
 * Verifies immediate playback versus queueing, the {@code MAX_QUEUE_SIZE} cap,
 * skipping, clearing, snapshot copies and size checks, using a mocked player
 * service and player.
 * </p>
 *
 * @author Dante Zulli (dantezulli2004@gmail.com)
 */
@ExtendWith(MockitoExtension.class)
class QueueServiceTest {

    private static final String TRACK_AUTHOR = "test";
    private static final String TRACK_ID = "id";
    private static final String TITLE_TEST_SONG = "Test Song";
    private static final String TITLE_PLAYING = "Playing";
    private static final String TITLE_QUEUED = "Queued";
    private static final String TITLE_QUEUED_FORMAT = "Queued %d";
    private static final String TITLE_OVERFLOW = "Overflow";
    private static final String TITLE_CURRENT = "Current";
    private static final String TITLE_NEXT = "Next";
    private static final String TITLE_SONG_1 = "Song 1";
    private static final String TITLE_SONG_2 = "Song 2";
    private static final String TITLE_SONG = "Song";

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
     * Tests that idle playback starts immediately.
     * <p>
     * Given no track playing, when a track is added, then it is played directly.
     * </p>
     */
    @Test
    void addToQueue_whenNoTrackPlaying_playsTrackImmediately() {
        AudioTrack track = createMockTrack(TITLE_TEST_SONG);
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
        AudioTrack playingTrack = createMockTrack(TITLE_PLAYING);
        AudioTrack queuedTrack = createMockTrack(TITLE_QUEUED);

        lenient().when(audioPlayerService.getPlayer()).thenReturn(audioPlayer);
        lenient().when(audioPlayer.getPlayingTrack()).thenReturn(playingTrack);

        queueService.addToQueue(queuedTrack);

        assertEquals(1, queueService.getQueueSize());
        verify(audioPlayer, never()).playTrack(any(AudioTrack.class));
    }

    /**
     * Tests that a full queue rejects new tracks.
     * <p>
     * Given {@code MAX_QUEUE_SIZE} queued tracks, when another track is added,
     * then it is rejected and the queue size stays capped.
     * </p>
     */
    @Test
    void addToQueue_whenQueueFull_rejectsTrack() {
        AudioTrack playingTrack = createMockTrack(TITLE_PLAYING);
        when(audioPlayerService.getPlayer()).thenReturn(audioPlayer);
        when(audioPlayer.getPlayingTrack()).thenReturn(playingTrack);

        for (int i = 0; i < QueueService.MAX_QUEUE_SIZE; i++) {
            assertTrue(
                queueService.addToQueue(
                    createMockTrack(TITLE_QUEUED_FORMAT.formatted(i))
                )
            );
        }

        assertFalse(queueService.addToQueue(createMockTrack(TITLE_OVERFLOW)));
        assertEquals(QueueService.MAX_QUEUE_SIZE, queueService.getQueueSize());
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

        AudioTrack currentTrack = createMockTrack(TITLE_CURRENT);
        AudioTrack nextTrack = createMockTrack(TITLE_NEXT);
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

        queueService.addToQueue(createMockTrack(TITLE_SONG_1));
        queueService.addToQueue(createMockTrack(TITLE_SONG_2));
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

        queueService.addToQueue(createMockTrack(TITLE_SONG_1));
        queueService.addToQueue(createMockTrack(TITLE_SONG_2));

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

        queueService.addToQueue(createMockTrack(TITLE_SONG));

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

        queueService.addToQueue(createMockTrack(TITLE_SONG_1));
        queueService.addToQueue(createMockTrack(TITLE_SONG_2));

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
        AudioTrack playingTrack = createMockTrack(TITLE_PLAYING);
        AudioTrack queuedTrack = createMockTrack(TITLE_QUEUED);

        when(audioPlayerService.getPlayer()).thenReturn(audioPlayer);
        when(audioPlayer.getPlayingTrack()).thenReturn(playingTrack);

        queueService.addToQueue(queuedTrack);

        queueService.playNextTrack();

        verify(audioPlayer).playTrack(queuedTrack);
        assertEquals(0, queueService.getQueueSize());
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
            TRACK_AUTHOR,
            0L,
            TRACK_ID,
            false,
            null
        );
        lenient().doReturn(info).when(track).getInfo();
        return track;
    }
}
