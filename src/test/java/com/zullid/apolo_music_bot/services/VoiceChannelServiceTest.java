package com.zullid.apolo_music_bot.services;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.managers.AudioManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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

    @BeforeEach
    void setUp() {
        voiceChannelService = new VoiceChannelService(audioPlayerService);
        lenient().when(audioPlayerService.getPlayer()).thenReturn(audioPlayer);
    }

    @Test
    void joinVoiceChannel_userNotInVoiceChannel_returnsFalse() {
        when(member.getVoiceState()).thenReturn(null);

        boolean result = voiceChannelService.joinVoiceChannel(member);

        assertFalse(result);
    }

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

    @Test
    void leaveVoiceChannel_whenConnected_closesConnection() {
        when(guild.getAudioManager()).thenReturn(audioManager);
        when(audioManager.isConnected()).thenReturn(true);

        voiceChannelService.leaveVoiceChannel(guild);

        verify(audioManager).setSelfDeafened(false);
        verify(audioManager).closeAudioConnection();
    }

    @Test
    void leaveVoiceChannel_whenNotConnected_doesNothing() {
        when(guild.getAudioManager()).thenReturn(audioManager);
        when(audioManager.isConnected()).thenReturn(false);

        voiceChannelService.leaveVoiceChannel(guild);

        verify(audioManager, never()).closeAudioConnection();
    }

    @Test
    void isConnected_whenConnected_returnsTrue() {
        when(guild.getAudioManager()).thenReturn(audioManager);
        when(audioManager.isConnected()).thenReturn(true);

        boolean result = voiceChannelService.isConnected(guild);

        assertTrue(result);
    }

    @Test
    void isConnected_whenNotConnected_returnsFalse() {
        when(guild.getAudioManager()).thenReturn(audioManager);
        when(audioManager.isConnected()).thenReturn(false);

        boolean result = voiceChannelService.isConnected(guild);

        assertFalse(result);
    }

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