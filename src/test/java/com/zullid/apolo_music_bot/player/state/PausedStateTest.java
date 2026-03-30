package com.zullid.apolo_music_bot.player.state;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.zullid.apolo_music_bot.player.Player;
import com.zullid.apolo_music_bot.services.AudioPlayerService;
import com.zullid.apolo_music_bot.services.QueueService;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PausedStateTest {

    @Mock
    private Player player;

    @Mock
    private SlashCommandInteractionEvent event;

    @Mock
    private ReplyCallbackAction replyAction;

    @Mock
    private AudioPlayerService audioPlayerService;

    @Mock
    private QueueService queueService;

    @Mock
    private AudioPlayer audioPlayer;

    private PausedState pausedState;

    @BeforeEach
    void setUp() {
        when(player.getAudioPlayerService()).thenReturn(audioPlayerService);
        when(player.getQueueService()).thenReturn(queueService);
        when(audioPlayerService.getPlayer()).thenReturn(audioPlayer);
        
        pausedState = new PausedState(player);
        
        lenient().when(event.reply(anyString())).thenReturn(replyAction);
    }

    @Test
    void onPlay_repliesCannotPlayWhilePaused() {
        pausedState.onPlay(event);

        verify(event).reply("Cannot play while paused. Use resume.");
    }

    @Test
    void onPause_repliesAlreadyPausedMessage() {
        pausedState.onPause(event);

        verify(event).reply("Player is already paused!");
    }

    @Test
    void onResume_unpausesPlayerAndTransitionsToPlayingState() {
        pausedState.onResume(event);

        verify(audioPlayer).setPaused(false);
        verify(event).reply("Resumed the current track");
        verify(player).setState(any(PlayingState.class));
    }

    @Test
    void onStop_clearsQueueStopsTrackAndTransitionsToReadyState() {
        pausedState.onStop(event);

        verify(queueService).clearQueue();
        verify(audioPlayer).stopTrack();
        verify(event).reply("Stopped playback and cleared the queue");
        verify(player).setState(any(ReadyState.class));
    }

    @Test
    void onSkip_repliesCannotSkipWhilePaused() {
        pausedState.onSkip(event);

        verify(event).reply("Cannot skip while paused. Use resume.");
    }
}