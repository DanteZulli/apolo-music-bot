package com.zullid.apolo_music_bot.services;

import com.zullid.apolo_music_bot.handlers.AudioPlayerSendHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.managers.AudioManager;
import org.springframework.stereotype.Service;

/**
 * Service responsible for managing voice channel connections.
 * <p>
 * This service handles joining and leaving voice channels in Discord guilds,
 * setting up the audio sending handler, and checking connection status.
 * Joining also self-deafens the bot to reduce bandwidth and indicate it only
 * sends audio. Callers are expected to ensure the bot ends up in the same
 * channel as the requesting user before operating.
 * </p>
 *
 * @see AudioPlayerService
 * @author Dante Zulli (dantezulli2004@gmail.com)
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class VoiceChannelService {

    private final AudioPlayerService audioPlayerService;

    /**
     * Joins the voice channel of the given member.
     * <p>
     * Returns {@code false} when the member is not in a voice channel. When the bot
     * is already connected, the join is skipped and {@code true} is returned.
     * Otherwise, installs the audio sending handler, opens the connection and
     * self-deafens the bot.
     * </p>
     *
     * @param member the guild member requesting playback, must have voice state
     * @return {@code true} if the bot is (or is now) connected, {@code false} otherwise
     */
    public boolean joinVoiceChannel(Member member) {
        if (
            member.getVoiceState() == null ||
            !member.getVoiceState().inAudioChannel()
        ) {
            log.warn("User {} is not in a voice channel", member.getId());
            return false;
        }

        AudioManager audioManager = member.getGuild().getAudioManager();

        if (audioManager.isConnected()) {
            log.debug("Already connected to voice channel, skipping join");
            return true;
        }

        VoiceChannel voiceChannel = member
            .getVoiceState()
            .getChannel()
            .asVoiceChannel();

        log.info(
            "Joining voice channel: {} (guild: {})",
            voiceChannel.getName(),
            member.getGuild().getName()
        );

        audioManager.setSendingHandler(
            new AudioPlayerSendHandler(audioPlayerService.getPlayer())
        );
        audioManager.openAudioConnection(voiceChannel);
        audioManager.setSelfDeafened(true);

        log.info("Bot joined voice channel: {}", voiceChannel.getName());
        return true;
    }

    /**
     * Leaves the voice channel of the given guild.
     * <p>
     * Disables self-deafen and closes the audio connection when connected.
     * Does nothing when already disconnected.
     * </p>
     *
     * @param guild the guild to disconnect from
     */
    public void leaveVoiceChannel(Guild guild) {
        AudioManager audioManager = guild.getAudioManager();
        if (audioManager.isConnected()) {
            audioManager.setSelfDeafened(false);
            audioManager.closeAudioConnection();
            log.info("Bot left voice channel in guild: {}", guild.getName());
        }
    }

    /**
     * Checks whether the bot is connected to a voice channel in the given guild.
     *
     * @param guild the guild to check
     * @return {@code true} if connected, {@code false} otherwise
     */
    public boolean isConnected(Guild guild) {
        return guild.getAudioManager().isConnected();
    }
}
