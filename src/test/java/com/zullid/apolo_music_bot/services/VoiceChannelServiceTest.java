package com.zullid.apolo_music_bot.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.managers.AudioManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for {@link VoiceChannelService}.
 * <p>
 * Verifies joining, leaving and connection checks with mocked guild, member and audio manager, including self-deafen setup.
 * </p>
 *
 * @author Dante Zulli (dantezulli2004@gmail.com)
 */
@ExtendWith(MockitoExtension.class)
class VoiceChannelServiceTest {

    @Mock
    private AudioPlayerService audioPlayerService;

    @Mock
    private Member member;

    @Mock
    private Guild guild;

    @Mock
    private AudioManager audioManager;

    @Mock
    private GuildVoiceState voiceState;

    @Mock
    private VoiceChannel voiceChannel;

    @Mock
    private AudioChannelUnion channelUnion;

    @Mock
    private AudioPlayer audioPlayer;

    private VoiceChannelService voiceChannelService;

    /**
     * Initializes the service with a mocked player service before each test.
     */
    @BeforeEach
    void setUp() {
        voiceChannelService = new VoiceChannelService(audioPlayerService);
        lenient().when(audioPlayerService.getPlayer()).thenReturn(audioPlayer);
    }

    /**
     * Tests that missing voice state rejects the join.
     * <p>
     * Given null voice state, when join is requested, then it returns false.
     * </p>
     */
    @Test
    void joinVoiceChannel_userNotInVoiceChannel_returnsFalse() {
        when(member.getVoiceState()).thenReturn(null);

        boolean result = voiceChannelService.joinVoiceChannel(member);

        assertFalse(result);
    }

    /**
     * Tests that not being in a channel rejects the join.
     * <p>
     * Given the user outside voice channels, when join is requested, then it returns false.
     * </p>
     */
    @Test
    void joinVoiceChannel_userInGuildButNotInVoiceChannel_returnsFalse() {
        when(member.getVoiceState()).thenReturn(voiceState);
        when(voiceState.inAudioChannel()).thenReturn(false);

        boolean result = voiceChannelService.joinVoiceChannel(member);

        assertFalse(result);
    }

    /**
     * Tests that an existing connection short-circuits the join.
     * <p>
     * Given the bot already connected, when join is requested, then it returns true without reconnecting.
     * </p>
     */
    @Test
    void joinVoiceChannel_alreadyConnected_returnsTrue() {
        when(member.getVoiceState()).thenReturn(voiceState);
        when(voiceState.inAudioChannel()).thenReturn(true);
        when(member.getGuild()).thenReturn(guild);
        when(guild.getAudioManager()).thenReturn(audioManager);
        when(audioManager.isConnected()).thenReturn(true);

        boolean result = voiceChannelService.joinVoiceChannel(member);

        assertTrue(result);
    }

    /**
     * Tests that leaving disables deafen and disconnects.
     * <p>
     * Given an active connection, when leave is requested, then self-deafen is cleared and the connection closes.
     * </p>
     */
    @Test
    void leaveVoiceChannel_whenConnected_closesConnection() {
        when(guild.getAudioManager()).thenReturn(audioManager);
        when(audioManager.isConnected()).thenReturn(true);

        voiceChannelService.leaveVoiceChannel(guild);

        verify(audioManager).setSelfDeafened(false);
        verify(audioManager).closeAudioConnection();
    }

    /**
     * Tests that leaving while disconnected is a no-op.
     * <p>
     * Given no connection, when leave is requested, then no disconnect happens.
     * </p>
     */
    @Test
    void leaveVoiceChannel_whenNotConnected_doesNothing() {
        when(guild.getAudioManager()).thenReturn(audioManager);
        when(audioManager.isConnected()).thenReturn(false);

        voiceChannelService.leaveVoiceChannel(guild);

        verify(audioManager, never()).closeAudioConnection();
    }

    /**
     * Tests that an active connection reports connected.
     * <p>
     * Given a connected manager, when checked, then it returns true.
     * </p>
     */
    @Test
    void isConnected_whenConnected_returnsTrue() {
        when(guild.getAudioManager()).thenReturn(audioManager);
        when(audioManager.isConnected()).thenReturn(true);

        boolean result = voiceChannelService.isConnected(guild);

        assertTrue(result);
    }

    /**
     * Tests that a disconnected manager reports disconnected.
     * <p>
     * Given a disconnected manager, when checked, then it returns false.
     * </p>
     */
    @Test
    void isConnected_whenNotConnected_returnsFalse() {
        when(guild.getAudioManager()).thenReturn(audioManager);
        when(audioManager.isConnected()).thenReturn(false);

        boolean result = voiceChannelService.isConnected(guild);

        assertFalse(result);
    }

    /**
     * Tests that a fresh join wires audio and deafens.
     * <p>
     * Given the user in voice and the bot disconnected, when join is requested, then the handler is set, the connection opens and the bot deafens itself.
     * </p>
     */
    @Test
    void joinVoiceChannel_successfullyJoinsAndConfiguresAudio() {
        when(member.getVoiceState()).thenReturn(voiceState);
        when(voiceState.inAudioChannel()).thenReturn(true);
        when(voiceState.getChannel()).thenReturn(channelUnion);
        when(channelUnion.asVoiceChannel()).thenReturn(voiceChannel);
        when(member.getGuild()).thenReturn(guild);
        when(guild.getAudioManager()).thenReturn(audioManager);
        when(audioManager.isConnected()).thenReturn(false);
        when(voiceChannel.getName()).thenReturn("Test Channel");

        boolean result = voiceChannelService.joinVoiceChannel(member);

        assertTrue(result);
        verify(audioManager).setSendingHandler(any());
        verify(audioManager).openAudioConnection(voiceChannel);
        verify(audioManager).setSelfDeafened(true);
    }
}
