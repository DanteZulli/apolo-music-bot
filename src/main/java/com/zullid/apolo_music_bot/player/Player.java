package com.zullid.apolo_music_bot.player;

import com.zullid.apolo_music_bot.player.state.PlayerState;
import com.zullid.apolo_music_bot.player.state.ReadyState;
import com.zullid.apolo_music_bot.services.AudioPlayerService;
import com.zullid.apolo_music_bot.services.QueueService;
import com.zullid.apolo_music_bot.services.VoiceChannelService;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Component;

/**
 * The main player component managing music playback states.
 * <p>
 * This class implements the State pattern to handle different playback states
 * (Ready, Playing, Paused). It delegates command handling to the current state
 * and ensures the bot is in the same voice channel as the user before executing
 * commands.
 * </p>
 *
 * @author Dante Zulli (dantezulli2004@gmail.com)
 */
@Slf4j
@Getter
@Setter
@Component
@RequiredArgsConstructor
public class Player {

    private final AudioPlayerService audioPlayerService;
    private final QueueService queueService;
    private final VoiceChannelService voiceChannelService;

    private PlayerState state;

    /**
     * Initializes the player in {@code ReadyState} after dependency injection.
     */
    @PostConstruct
    public void init() {
        this.state = new ReadyState(this);
    }

    /**
     * Handles the play command after ensuring voice channel presence.
     *
     * @param event the slash command interaction
     */
    public void play(SlashCommandInteractionEvent event) {
        checkVoiceChannel(event);
        state.onPlay(event);
    }

    /**
     * Handles the pause command after ensuring voice channel presence.
     *
     * @param event the slash command interaction
     */
    public void pause(SlashCommandInteractionEvent event) {
        checkVoiceChannel(event);
        state.onPause(event);
    }

    /**
     * Handles the resume command after ensuring voice channel presence.
     *
     * @param event the slash command interaction
     */
    public void resume(SlashCommandInteractionEvent event) {
        checkVoiceChannel(event);
        state.onResume(event);
    }

    /**
     * Handles the stop command after ensuring voice channel presence.
     *
     * @param event the slash command interaction
     */
    public void stop(SlashCommandInteractionEvent event) {
        checkVoiceChannel(event);
        state.onStop(event);
    }

    /**
     * Handles the skip command after ensuring voice channel presence.
     *
     * @param event the slash command interaction
     */
    public void skip(SlashCommandInteractionEvent event) {
        checkVoiceChannel(event);
        state.onSkip(event);
    }

    /**
     * Replies with the list of available commands.
     *
     * @param event the slash command interaction
     */
    public void help(SlashCommandInteractionEvent event) {
        String helpMessage = """
        **Available Commands:**

        `/play <query>` - Plays a song from any source or adds it to the queue
        `/pause` - Pauses the current playback
        `/resume` - Resumes the paused playback
        `/stop` - Stops the playback and clears the queue
        `/skip` - Skips the current song and moves to the next one in the queue
        `/queue` - Shows the current queue
        `/help` - Shows this help message
        """;
        event.reply(helpMessage).setEphemeral(true).queue();
    }

    /**
     * Replies with the current track and queued tracks.
     * <p>
     * Titles are truncated to {@code MAX_TITLE_LENGTH} (32) and at most 50 queued
     * tracks are shown so the reply stays within the Discord 2000-character message
     * limit.
     * </p>
     *
     * @param event the slash command interaction
     */
    public void queue(SlashCommandInteractionEvent event) {
        var player = audioPlayerService.getPlayer();
        var currentTrack = player.getPlayingTrack();
        var queueList = queueService.getQueueList();

        StringBuilder sb = new StringBuilder();

        // N = 32 based on calculation to fit 51 tracks (1 playing + 50 queue) within 2000 chars
        final int MAX_TITLE_LENGTH = 32;

        if (currentTrack != null) {
            sb.append("**Now Playing:** ")
                .append(
                    truncate(currentTrack.getInfo().title, MAX_TITLE_LENGTH)
                )
                .append("\n\n");
        } else {
            sb.append("**Now Playing:** Nothing\n\n");
        }

        sb.append("**Queue:**\n");

        if (queueList.isEmpty()) {
            sb.append("  (empty)");
        } else {
            int maxDisplay = Math.min(50, queueList.size());
            for (int i = 0; i < maxDisplay; i++) {
                sb.append(i + 1)
                    .append(". ")
                    .append(
                        truncate(
                            queueList.get(i).getInfo().title,
                            MAX_TITLE_LENGTH
                        )
                    )
                    .append("\n");
            }

            int remaining = queueList.size() - 50;
            if (remaining > 0) {
                sb.append("... and ").append(remaining).append(" more song(s)");
            }
        }

        event.reply(sb.toString()).setEphemeral(true).queue();
    }

    /**
     * Truncates a track title to the given maximum length.
     *
     * @param text the title to truncate, may be {@code null}
     * @param maxLength the maximum length including the {@code ...} suffix
     * @return the truncated title, or {@code "Unknown Title"} when {@code text} is {@code null}
     */
    private String truncate(String text, int maxLength) {
        if (text == null) return "Unknown Title";
        return text.length() > maxLength
            ? text.substring(0, maxLength - 3) + "..."
            : text;
    }

    /**
     * Ensures the bot is in the same voice channel as the requesting user.
     * <p>
     * When the bot is not connected, it attempts to auto-join the user's channel.
     * If the user is not in a channel or the join fails, an error reply is sent.
     * </p>
     *
     * @param event the slash command interaction carrying guild and member info
     */
    void checkVoiceChannel(SlashCommandInteractionEvent event) {
        log.debug(
            "Voice precheck - botConnected: {} | userInChannel: {}",
            voiceChannelService.isConnected(event.getGuild()),
            event.getMember().getVoiceState() != null &&
                event.getMember().getVoiceState().inAudioChannel()
        );

        if (
            !voiceChannelService.isConnected(event.getGuild()) &&
            !voiceChannelService.joinVoiceChannel(event.getMember())
        ) {
            event.reply("You need to be in a voice channel first!").queue();
        }
    }
}
