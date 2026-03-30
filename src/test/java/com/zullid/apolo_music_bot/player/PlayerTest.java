package com.zullid.apolo_music_bot.player;

import com.zullid.apolo_music_bot.player.state.PlayerState;
import com.zullid.apolo_music_bot.player.state.ReadyState;
import com.zullid.apolo_music_bot.services.AudioPlayerService;
import com.zullid.apolo_music_bot.services.QueueService;
import com.zullid.apolo_music_bot.services.VoiceChannelService;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PlayerTest {

    @Mock
    private AudioPlayerService audioPlayerService;

    @Mock
    private QueueService queueService;

    @Mock
    private VoiceChannelService voiceChannelService;

    @Mock
    private SlashCommandInteractionEvent event;

    @Mock
    private Guild guild;

    @Mock
    private Member member;

    @Mock
    private GuildVoiceState voiceState;

    @Mock
    private AudioPlayer audioPlayer;

    @Mock
    private ReplyCallbackAction replyAction;

    @InjectMocks
    private Player player;

    @BeforeEach
    void setUp() {
        when(event.getGuild()).thenReturn(guild);
        when(event.getMember()).thenReturn(member);
        when(member.getVoiceState()).thenReturn(voiceState);
        when(audioPlayerService.getPlayer()).thenReturn(audioPlayer);
        lenient().when(event.reply(anyString())).thenReturn(replyAction);
        lenient().when(replyAction.setEphemeral(true)).thenReturn(replyAction);
        lenient().doNothing().when(replyAction).queue();
    }

    @Test
    void init_setsInitialStateToReadyState() {
        player.setState(new ReadyState(player));
        assertNotNull(player.getState());
        assertTrue(player.getState() instanceof ReadyState);
    }

    @Test
    void play_delegatesToCurrentState() {
        PlayerState mockState = mock(PlayerState.class);
        player.setState(mockState);
        player.play(event);
        verify(mockState).onPlay(event);
    }

    @Test
    void pause_delegatesToCurrentState() {
        PlayerState mockState = mock(PlayerState.class);
        player.setState(mockState);
        player.pause(event);
        verify(mockState).onPause(event);
    }

    @Test
    void resume_delegatesToCurrentState() {
        PlayerState mockState = mock(PlayerState.class);
        player.setState(mockState);
        player.resume(event);
        verify(mockState).onResume(event);
    }

    @Test
    void stop_delegatesToCurrentState() {
        PlayerState mockState = mock(PlayerState.class);
        player.setState(mockState);
        player.stop(event);
        verify(mockState).onStop(event);
    }

    @Test
    void skip_delegatesToCurrentState() {
        PlayerState mockState = mock(PlayerState.class);
        player.setState(mockState);
        player.skip(event);
        verify(mockState).onSkip(event);
    }

    @Test
    void help_sendsHelpMessage() {
        player.help(event);
        verify(event).reply(argThat(new ArgumentMatcher<String>() {
            @Override
            public boolean matches(String message) {
                return message.contains("**Available Commands:**") &&
                       message.contains("`/play <query>` - Plays a song from any source or adds it to the queue") &&
                       message.contains("`/pause` - Pauses the current playback") &&
                       message.contains("`/resume` - Resumes the paused playback") &&
                       message.contains("`/stop` - Stops the playback and clears the queue") &&
                       message.contains("`/skip` - Skips the current song and moves to the next one in the queue") &&
                       message.contains("`/queue` - Shows the current queue") &&
                       message.contains("`/help` - Shows this help message");
            }
        }));
        verify(replyAction).setEphemeral(true);
        verify(replyAction).queue();
    }

    @Test
    void queue_withNoTracks_showsEmptyQueue() {
        when(audioPlayer.getPlayingTrack()).thenReturn(null);
        when(queueService.getQueueList()).thenReturn(Collections.emptyList());
        player.queue(event);
        verify(event).reply(argThat(new ArgumentMatcher<String>() {
            @Override
            public boolean matches(String message) {
                return message.equals("**Now Playing:** Nothing\n\n**Queue:**\n  (empty)");
            }
        }));
        verify(replyAction).setEphemeral(true);
        verify(replyAction).queue();
    }

    @Test
    void checkVoiceChannel_whenUserNotInVoiceChannel_repliesError() {
        when(voiceChannelService.isConnected(guild)).thenReturn(false);
        when(member.getVoiceState()).thenReturn(null);
        player.checkVoiceChannel(event);
        verify(event).reply("You need to be in a voice channel first!");
        verify(replyAction).queue();
    }

    @Test
    void checkVoiceChannel_whenBotNotConnectedButUserInChannel_joinsAndDoesNotReplyError() {
        when(voiceChannelService.isConnected(guild)).thenReturn(false);
        when(member.getVoiceState()).thenReturn(voiceState);
        when(voiceState.inAudioChannel()).thenReturn(true);
        when(voiceChannelService.joinVoiceChannel(member)).thenReturn(true);
        player.checkVoiceChannel(event);
        verify(voiceChannelService).joinVoiceChannel(member);
        verify(event, never()).reply(anyString());
    }

    @Test
    void checkVoiceChannel_whenBotNotConnectedAndUserInChannelButJoinFails_repliesError() {
        when(voiceChannelService.isConnected(guild)).thenReturn(false);
        when(member.getVoiceState()).thenReturn(voiceState);
        when(voiceState.inAudioChannel()).thenReturn(true);
        when(voiceChannelService.joinVoiceChannel(member)).thenReturn(false);
        player.checkVoiceChannel(event);
        verify(event).reply("You need to be in a voice channel first!");
        verify(replyAction).queue();
    }

    @Test
    void checkVoiceChannel_whenBotAlreadyConnected_doesNotAttemptToJoin() {
        when(voiceChannelService.isConnected(guild)).thenReturn(true);
        when(member.getVoiceState()).thenReturn(voiceState);
        when(voiceState.inAudioChannel()).thenReturn(true);
        player.checkVoiceChannel(event);
        verify(voiceChannelService, never()).joinVoiceChannel(any());
        verify(event, never()).reply(anyString());
    }

    @Test
    void queue_withPlayingTrack_showsNowPlaying() {
        AudioTrackInfo info = new AudioTrackInfo("Current Song", "test", 0L, "id", false, null);
        AudioTrack track = mock(AudioTrack.class);
        when(track.getInfo()).thenReturn(info);
        when(audioPlayer.getPlayingTrack()).thenReturn(track);
        when(queueService.getQueueList()).thenReturn(Collections.emptyList());

        player.queue(event);

        verify(event).reply(argThat(new ArgumentMatcher<String>() {
            @Override
            public boolean matches(String message) {
                return message.contains("**Now Playing:** Current Song");
            }
        }));
    }

    @Test
    void queue_withTracks_showsQueueList() {
        AudioTrackInfo info = new AudioTrackInfo("Playing", "test", 0L, "id", false, null);
        AudioTrack playing = mock(AudioTrack.class);
        when(playing.getInfo()).thenReturn(info);
        when(audioPlayer.getPlayingTrack()).thenReturn(playing);

        AudioTrackInfo q1 = new AudioTrackInfo("Next Song", "test", 0L, "id2", false, null);
        AudioTrack track1 = mock(AudioTrack.class);
        when(track1.getInfo()).thenReturn(q1);
        when(queueService.getQueueList()).thenReturn(List.of(track1));

        player.queue(event);

        verify(event).reply(argThat(new ArgumentMatcher<String>() {
            @Override
            public boolean matches(String message) {
                return message.contains("1. Next Song");
            }
        }));
    }

    @Test
    void queue_withLongTitle_truncatesToMaxLength() {
        String longTitle = "This is a very long song title that should be truncated because it exceeds the maximum length";
        AudioTrackInfo info = new AudioTrackInfo(longTitle, "test", 0L, "id", false, null);
        AudioTrack track = mock(AudioTrack.class);
        when(track.getInfo()).thenReturn(info);
        when(audioPlayer.getPlayingTrack()).thenReturn(track);
        when(queueService.getQueueList()).thenReturn(Collections.emptyList());

        player.queue(event);

        verify(event).reply(argThat(new ArgumentMatcher<String>() {
            @Override
            public boolean matches(String message) {
                return message.contains("This is a very long song titl...");
            }
        }));
    }

    @Test
    void queue_withFiftyPlusTracks_showsRemainingCount() {
        AudioTrackInfo info = new AudioTrackInfo("Playing", "test", 0L, "id", false, null);
        AudioTrack playing = mock(AudioTrack.class);
        when(playing.getInfo()).thenReturn(info);
        when(audioPlayer.getPlayingTrack()).thenReturn(playing);

        List<AudioTrack> tracks = new ArrayList<>();
        for (int i = 0; i < 55; i++) {
            AudioTrackInfo qi = new AudioTrackInfo("Track " + i, "test", 0L, "id" + i, false, null);
            AudioTrack t = mock(AudioTrack.class);
            when(t.getInfo()).thenReturn(qi);
            tracks.add(t);
        }
        when(queueService.getQueueList()).thenReturn(tracks);

        player.queue(event);

        verify(event).reply(argThat(new ArgumentMatcher<String>() {
            @Override
            public boolean matches(String message) {
                return message.contains("... and 5 more song(s)");
            }
        }));
    }

    @Test
    void queue_withNullTrackTitle_showsUnknownTitle() {
        AudioTrackInfo info = new AudioTrackInfo("Playing", "test", 0L, "id", false, null);
        AudioTrack playing = mock(AudioTrack.class);
        when(playing.getInfo()).thenReturn(info);
        when(audioPlayer.getPlayingTrack()).thenReturn(playing);

        AudioTrackInfo nullInfo = new AudioTrackInfo(null, "test", 0L, "id2", false, null);
        AudioTrack nullTrack = mock(AudioTrack.class);
        when(nullTrack.getInfo()).thenReturn(nullInfo);
        when(queueService.getQueueList()).thenReturn(List.of(nullTrack));

        player.queue(event);

        verify(event).reply(argThat(new ArgumentMatcher<String>() {
            @Override
            public boolean matches(String message) {
                return message.contains("1. Unknown Title");
            }
        }));
    }
}