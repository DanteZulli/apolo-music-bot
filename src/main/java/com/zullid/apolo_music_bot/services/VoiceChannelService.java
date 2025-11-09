package com.zullid.apolo_music_bot.services;

import org.springframework.stereotype.Service;

import com.zullid.apolo_music_bot.handlers.AudioPlayerSendHandler;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.managers.AudioManager;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service responsible for managing voice channel connections.
 * 
 * @see AudioPlayerService
 * @author Dante Zulli (dantezulli2004@gmail.com)
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class VoiceChannelService {

    private final AudioPlayerService audioPlayerService;

    public boolean joinVoiceChannel(Member member) {
        if (member.getVoiceState() == null || !member.getVoiceState().inAudioChannel()) {
            return false;
        }

        VoiceChannel voiceChannel = member.getVoiceState().getChannel().asVoiceChannel();
        AudioManager audioManager = member.getGuild().getAudioManager();

        audioManager.setSendingHandler(new AudioPlayerSendHandler(audioPlayerService.getPlayer()));
        audioManager.openAudioConnection(voiceChannel);
        audioManager.setSelfDeafened(true);

        log.info("Bot joined voice channel: {}", voiceChannel.getName());
        return true;
    }

    public void leaveVoiceChannel(Guild guild) {
        AudioManager audioManager = guild.getAudioManager();
        if (audioManager.isConnected()) {
            audioManager.setSelfDeafened(false);
            audioManager.closeAudioConnection();
            log.info("Bot left voice channel in guild: {}", guild.getName());
        }
    }

    public boolean isConnected(Guild guild) {
        return guild.getAudioManager().isConnected();
    }
}