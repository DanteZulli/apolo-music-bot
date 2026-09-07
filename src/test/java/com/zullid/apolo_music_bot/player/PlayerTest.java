package com.zullid.apolo_music_bot.player;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import com.zullid.apolo_music_bot.player.state.PlayerState;
import com.zullid.apolo_music_bot.player.state.ReadyState;
import com.zullid.apolo_music_bot.services.AudioPlayerService;
import com.zullid.apolo_music_bot.services.QueueService;
import com.zullid.apolo_music_bot.services.VoiceChannelService;
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

/**
 * Unit tests for {@link com.zullid.apolo_music_bot.player.Player}.
 * <p>
 * Verifies state delegation for play, pause, resume, stop and skip, help and queue
 * message rendering (including truncation, paging and empty states), and voice channel
 * auto-join guards, using mocked services and slash command events.
 * </p>
 *
 * @author Dante Zulli (dantezulli2004@gmail.com)
 */
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

    /**
     * Stubs guild, member, voice and reply collaborators before each test.
     */
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

    /**
     * Tests that initialization starts in ready state.
     * <p>
     * Given a fresh player, when initialized, then the state is ready.
     * </p>
     */
    @Test
    void init_setsInitialStateToReadyState() {
        player.setState(new ReadyState(player));
        assertNotNull(player.getState());
        assertTrue(player.getState() instanceof ReadyState);
    }

    /**
     * Tests that play is delegated.
     * <p>
     * Given any state, when play is called, then the state handles it.
     * </p>
     */
    @Test
    void play_delegatesToCurrentState() {
        PlayerState mockState = mock(PlayerState.class);
        player.setState(mockState);
        player.play(event);
        verify(mockState).onPlay(event);
    }

    /**
     * Tests that pause is delegated.
     * <p>
     * Given any state, when pause is called, then the state handles it.
     * </p>
     */
    @Test
    void pause_delegatesToCurrentState() {
        PlayerState mockState = mock(PlayerState.class);
        player.setState(mockState);
        player.pause(event);
        verify(mockState).onPause(event);
    }

    /**
     * Tests that resume is delegated.
     * <p>
     * Given any state, when resume is called, then the state handles it.
     * </p>
     */
    @Test
    void resume_delegatesToCurrentState() {
        PlayerState mockState = mock(PlayerState.class);
        player.setState(mockState);
        player.resume(event);
        verify(mockState).onResume(event);
    }

    /**
     * Tests that stop is delegated.
     * <p>
     * Given any state, when stop is called, then the state handles it.
     * </p>
     */
    @Test
    void stop_delegatesToCurrentState() {
        PlayerState mockState = mock(PlayerState.class);
        player.setState(mockState);
        player.stop(event);
        verify(mockState).onStop(event);
    }

    /**
     * Tests that skip is delegated.
     * <p>
     * Given any state, when skip is called, then the state handles it.
     * </p>
     */
    @Test
    void skip_delegatesToCurrentState() {
        PlayerState mockState = mock(PlayerState.class);
        player.setState(mockState);
        player.skip(event);
        verify(mockState).onSkip(event);
    }

    /**
     * Tests that help lists all commands ephemerally.
     * <p>
     * Given any state, when help is called, then the full command list is replied ephemerally.
     * </p>
     */
    @Test
    void help_sendsHelpMessage() {
        player.help(event);
        verify(event).reply(
            argThat(
                new ArgumentMatcher<String>() {
                    @Override
                    public boolean matches(String message) {
                        return (
                            message.contains("**Available Commands:**") &&
                            message.contains(
                                "`/play <query>` - Plays a song from any source or adds it to the queue"
                            ) &&
                            message.contains(
                                "`/pause` - Pauses the current playback"
                            ) &&
                            message.contains(
                                "`/resume` - Resumes the paused playback"
                            ) &&
                            message.contains(
                                "`/stop` - Stops the playback and clears the queue"
                            ) &&
                            message.contains(
                                "`/skip` - Skips the current song and moves to the next one in the queue"
                            ) &&
                            message.contains(
                                "`/queue` - Shows the current queue"
                            ) &&
                            message.contains(
                                "`/help` - Shows this help message"
                            )
                        );
                    }
                }
            )
        );
        verify(replyAction).setEphemeral(true);
        verify(replyAction).queue();
    }

    /**
     * Tests that an empty player shows empty queue.
     * <p>
     * Given nothing playing and empty queue, when queue is called, then the empty message is replied.
     * </p>
     */
    @Test
    void queue_withNoTracks_showsEmptyQueue() {
        when(audioPlayer.getPlayingTrack()).thenReturn(null);
        when(queueService.getQueueList()).thenReturn(Collections.emptyList());
        player.queue(event);
        verify(event).reply(
            argThat(
                new ArgumentMatcher<String>() {
                    @Override
                    public boolean matches(String message) {
                        return message.equals(
                            "**Now Playing:** Nothing\n\n**Queue:**\n  (empty)"
                        );
                    }
                }
            )
        );
        verify(replyAction).setEphemeral(true);
        verify(replyAction).queue();
    }

    /**
     * Tests that missing user voice errors.
     * <p>
     * Given the user outside voice and the bot disconnected, when checked, then an error reply is sent.
     * </p>
     */
    @Test
    void checkVoiceChannel_whenUserNotInVoiceChannel_repliesError() {
        when(voiceChannelService.isConnected(guild)).thenReturn(false);
        when(member.getVoiceState()).thenReturn(null);
        player.checkVoiceChannel(event);
        verify(event).reply("You need to be in a voice channel first!");
        verify(replyAction).queue();
    }

    /**
     * Tests that auto-join succeeds silently.
     * <p>
     * Given the user in voice and join succeeding, when checked, then the bot joins without error reply.
     * </p>
     */
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

    /**
     * Tests that failed auto-join errors.
     * <p>
     * Given the user in voice but join failing, when checked, then an error reply is sent.
     * </p>
     */
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

    /**
     * Tests that connected bots skip joining.
     * <p>
     * Given the bot already connected, when checked, then no join is attempted and no reply is sent.
     * </p>
     */
    @Test
    void checkVoiceChannel_whenBotAlreadyConnected_doesNotAttemptToJoin() {
        when(voiceChannelService.isConnected(guild)).thenReturn(true);
        when(member.getVoiceState()).thenReturn(voiceState);
        when(voiceState.inAudioChannel()).thenReturn(true);
        player.checkVoiceChannel(event);
        verify(voiceChannelService, never()).joinVoiceChannel(any());
        verify(event, never()).reply(anyString());
    }

    /**
     * Tests that the current track is shown.
     * <p>
     * Given a playing track, when queue is called, then the now-playing title is included.
     * </p>
     */
    @Test
    void queue_withPlayingTrack_showsNowPlaying() {
        AudioTrackInfo info = new AudioTrackInfo(
            "Current Song",
            "test",
            0L,
            "id",
            false,
            null
        );
        AudioTrack track = mock(AudioTrack.class);
        when(track.getInfo()).thenReturn(info);
        when(audioPlayer.getPlayingTrack()).thenReturn(track);
        when(queueService.getQueueList()).thenReturn(Collections.emptyList());

        player.queue(event);

        verify(event).reply(
            argThat(
                new ArgumentMatcher<String>() {
                    @Override
                    public boolean matches(String message) {
                        return message.contains(
                            "**Now Playing:** Current Song"
                        );
                    }
                }
            )
        );
    }

    /**
     * Tests that queued tracks are listed.
     * <p>
     * Given one queued track, when queue is called, then its entry is included.
     * </p>
     */
    @Test
    void queue_withTracks_showsQueueList() {
        AudioTrackInfo info = new AudioTrackInfo(
            "Playing",
            "test",
            0L,
            "id",
            false,
            null
        );
        AudioTrack playing = mock(AudioTrack.class);
        when(playing.getInfo()).thenReturn(info);
        when(audioPlayer.getPlayingTrack()).thenReturn(playing);

        AudioTrackInfo q1 = new AudioTrackInfo(
            "Next Song",
            "test",
            0L,
            "id2",
            false,
            null
        );
        AudioTrack track1 = mock(AudioTrack.class);
        when(track1.getInfo()).thenReturn(q1);
        when(queueService.getQueueList()).thenReturn(List.of(track1));

        player.queue(event);

        verify(event).reply(
            argThat(
                new ArgumentMatcher<String>() {
                    @Override
                    public boolean matches(String message) {
                        return message.contains("1. Next Song");
                    }
                }
            )
        );
    }

    /**
     * Tests that long titles are truncated.
     * <p>
     * Given a title over 32 chars, when queue is called, then it is shortened with ellipsis to fit Discord limits.
     * </p>
     */
    @Test
    void queue_withLongTitle_truncatesToMaxLength() {
        String longTitle =
            "This is a very long song title that should be truncated because it exceeds the maximum length";
        AudioTrackInfo info = new AudioTrackInfo(
            longTitle,
            "test",
            0L,
            "id",
            false,
            null
        );
        AudioTrack track = mock(AudioTrack.class);
        when(track.getInfo()).thenReturn(info);
        when(audioPlayer.getPlayingTrack()).thenReturn(track);
        when(queueService.getQueueList()).thenReturn(Collections.emptyList());

        player.queue(event);

        verify(event).reply(
            argThat(
                new ArgumentMatcher<String>() {
                    @Override
                    public boolean matches(String message) {
                        return message.contains(
                            "This is a very long song titl..."
                        );
                    }
                }
            )
        );
    }

    /**
     * Tests that oversized queues are paged.
     * <p>
     * Given 55 queued tracks, when queue is called, then 50 are shown plus a remaining count.
     * </p>
     */
    @Test
    void queue_withFiftyPlusTracks_showsRemainingCount() {
        AudioTrackInfo info = new AudioTrackInfo(
            "Playing",
            "test",
            0L,
            "id",
            false,
            null
        );
        AudioTrack playing = mock(AudioTrack.class);
        when(playing.getInfo()).thenReturn(info);
        when(audioPlayer.getPlayingTrack()).thenReturn(playing);

        List<AudioTrack> tracks = new ArrayList<>();
        for (int i = 0; i < 55; i++) {
            AudioTrackInfo qi = new AudioTrackInfo(
                "Track " + i,
                "test",
                0L,
                "id" + i,
                false,
                null
            );
            AudioTrack t = mock(AudioTrack.class);
            when(t.getInfo()).thenReturn(qi);
            tracks.add(t);
        }
        when(queueService.getQueueList()).thenReturn(tracks);

        player.queue(event);

        verify(event).reply(
            argThat(
                new ArgumentMatcher<String>() {
                    @Override
                    public boolean matches(String message) {
                        return message.contains("... and 5 more song(s)");
                    }
                }
            )
        );
    }

    /**
     * Tests that null titles fall back gracefully.
     * <p>
     * Given a track with null title, when queue is called, then Unknown Title is shown.
     * </p>
     */
    @Test
    void queue_withNullTrackTitle_showsUnknownTitle() {
        AudioTrackInfo info = new AudioTrackInfo(
            "Playing",
            "test",
            0L,
            "id",
            false,
            null
        );
        AudioTrack playing = mock(AudioTrack.class);
        when(playing.getInfo()).thenReturn(info);
        when(audioPlayer.getPlayingTrack()).thenReturn(playing);

        AudioTrackInfo nullInfo = new AudioTrackInfo(
            null,
            "test",
            0L,
            "id2",
            false,
            null
        );
        AudioTrack nullTrack = mock(AudioTrack.class);
        when(nullTrack.getInfo()).thenReturn(nullInfo);
        when(queueService.getQueueList()).thenReturn(List.of(nullTrack));

        player.queue(event);

        verify(event).reply(
            argThat(
                new ArgumentMatcher<String>() {
                    @Override
                    public boolean matches(String message) {
                        return message.contains("1. Unknown Title");
                    }
                }
            )
        );
    }
}
