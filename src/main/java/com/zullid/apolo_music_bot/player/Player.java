package com.zullid.apolo_music_bot.player;

import com.zullid.apolo_music_bot.player.state.PlayerState;
import com.zullid.apolo_music_bot.player.state.ReadyState;
import com.zullid.apolo_music_bot.services.AudioPlayerService;
import com.zullid.apolo_music_bot.services.QueueService;
import com.zullid.apolo_music_bot.services.VoiceChannelService;
import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
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

    private static final int MAX_TITLE_LENGTH = 32;
    private static final int ELLIPSIS_LENGTH = 3;
    private static final String UNKNOWN_TITLE = "Unknown Title";
    private static final String TRUNCATED_TITLE_FORMAT = "%s...";
    private static final String NEED_VOICE_MESSAGE =
        "You need to be in a voice channel first!";
    private static final String HELP_MESSAGE = """
        **Available Commands:**

        `/play <query>` - Plays a song from any source or adds it to the queue
        `/pause` - Pauses the current playback
        `/resume` - Resumes the paused playback
        `/stop` - Stops the playback and clears the queue
        `/skip` - Skips the current song and moves to the next one in the queue
        `/queue` - Shows the current queue
        `/help` - Shows this help message
        """;
    private static final String NOW_PLAYING_FORMAT = "**Now Playing:** %s\n\n";
    private static final String NOW_PLAYING_EMPTY =
        "**Now Playing:** Nothing\n\n";
    private static final String QUEUE_HEADER = "**Queue:**\n";
    private static final String QUEUE_EMPTY_ENTRY = "  (empty)";
    private static final String QUEUE_ENTRY_FORMAT = "%d. %s";
    private static final String MORE_SONGS_FORMAT = "... and %d more song(s)";
    private static final String LINE_SEPARATOR = "\n";
    private static final String TWO_PART_FORMAT = "%s%s";
    private static final String LOG_VOICE_PRECHECK_FORMAT =
        "Voice precheck - botConnected: {} | userInChannel: {}";

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
        event.reply(HELP_MESSAGE).setEphemeral(true).queue();
    }

    /**
     * Replies with the current track and queued tracks.
     * <p>
     * Titles are truncated to {@code MAX_TITLE_LENGTH} (32) and at most
     * {@code MAX_QUEUE_SIZE} queued tracks are shown so the reply stays within
     * the Discord 2000-character message limit.
     * </p>
     *
     * @param event the slash command interaction
     */
    public void queue(SlashCommandInteractionEvent event) {
        var player = audioPlayerService.getPlayer();
        var currentTrack = player.getPlayingTrack();
        var queueList = queueService.getQueueList();

        // N = 32 based on calculation to fit 51 tracks (1 playing + 50 queue) within 2000 chars
        String nowPlayingPart;
        if (currentTrack != null) {
            nowPlayingPart = NOW_PLAYING_FORMAT.formatted(
                truncate(currentTrack.getInfo().title, MAX_TITLE_LENGTH)
            );
        } else {
            nowPlayingPart = NOW_PLAYING_EMPTY;
        }

        List<String> entries = new ArrayList<>();
        if (queueList.isEmpty()) {
            entries.add(QUEUE_EMPTY_ENTRY);
        } else {
            int maxDisplay = Math.min(
                QueueService.MAX_QUEUE_SIZE,
                queueList.size()
            );
            for (int i = 0; i < maxDisplay; i++) {
                entries.add(
                    QUEUE_ENTRY_FORMAT.formatted(
                        i + 1,
                        truncate(
                            queueList.get(i).getInfo().title,
                            MAX_TITLE_LENGTH
                        )
                    )
                );
            }

            int remaining = queueList.size() - QueueService.MAX_QUEUE_SIZE;
            if (remaining > 0) {
                entries.add(MORE_SONGS_FORMAT.formatted(remaining));
            }
        }

        String queueSection = TWO_PART_FORMAT.formatted(
            QUEUE_HEADER,
            String.join(LINE_SEPARATOR, entries)
        );
        String reply = TWO_PART_FORMAT.formatted(nowPlayingPart, queueSection);

        event.reply(reply).setEphemeral(true).queue();
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
            LOG_VOICE_PRECHECK_FORMAT,
            voiceChannelService.isConnected(event.getGuild()),
            event.getMember().getVoiceState() != null &&
                event.getMember().getVoiceState().inAudioChannel()
        );

        if (
            !voiceChannelService.isConnected(event.getGuild()) &&
            !voiceChannelService.joinVoiceChannel(event.getMember())
        ) {
            event.reply(NEED_VOICE_MESSAGE).queue();
        }
    }

    /**
     * Truncates a track title to the given maximum length.
     *
     * @param text the title to truncate, may be {@code null}
     * @param maxLength the maximum length including the {@code ...} suffix
     * @return the truncated title, or {@code "Unknown Title"} when {@code text} is {@code null}
     */
    private String truncate(String text, int maxLength) {
        if (text == null) {
            return UNKNOWN_TITLE;
        }
        if (text.length() > maxLength) {
            return TRUNCATED_TITLE_FORMAT.formatted(
                text.substring(0, maxLength - ELLIPSIS_LENGTH)
            );
        }
        return text;
    }
}
