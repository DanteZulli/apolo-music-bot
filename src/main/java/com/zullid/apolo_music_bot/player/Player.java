package com.zullid.apolo_music_bot.player;

import org.springframework.stereotype.Component;

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

@Slf4j

/**
 * The main player component managing music playback states.
 * <p>
 * This class implements the State pattern to handle different playback states
 * (Ready, Playing, Paused). It delegates command handling to the current state
 * and ensures the user is in a voice channel before executing commands.
 * </p>
 *
 * @author Dante Zulli (dantezulli2004@gmail.com)
 */
@Getter
@Setter
@Component
@RequiredArgsConstructor
public class Player {

    private final AudioPlayerService audioPlayerService;
    private final QueueService queueService;
    private final VoiceChannelService voiceChannelService;

    private PlayerState state;

    @PostConstruct
    public void init() {
        this.state = new ReadyState(this);
    }

    public void play(SlashCommandInteractionEvent event) {
        checkVoiceChannel(event);
        state.onPlay(event);
    }

    public void pause(SlashCommandInteractionEvent event) {
        checkVoiceChannel(event);
        state.onPause(event);
    }

    public void resume(SlashCommandInteractionEvent event) {
        checkVoiceChannel(event);
        state.onResume(event);
    }

    public void stop(SlashCommandInteractionEvent event) {
        checkVoiceChannel(event);
        state.onStop(event);
    }

    public void skip(SlashCommandInteractionEvent event) {
        checkVoiceChannel(event);
        state.onSkip(event);
    }

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

    public void queue(SlashCommandInteractionEvent event) {
        var player = audioPlayerService.getPlayer();
        var currentTrack = player.getPlayingTrack();
        var queueList = queueService.getQueueList();

        StringBuilder sb = new StringBuilder();

        // N = 32 based on calculation to fit 51 tracks (1 playing + 50 queue) within 2000 chars
        final int MAX_TITLE_LENGTH = 32;

        if (currentTrack != null) {
            sb.append("**Now Playing:** ").append(truncate(currentTrack.getInfo().title, MAX_TITLE_LENGTH)).append("\n\n");
        } else {
            sb.append("**Now Playing:** Nothing\n\n");
        }

        sb.append("**Queue:**\n");

        if (queueList.isEmpty()) {
            sb.append("  (empty)");
        } else {
            int maxDisplay = Math.min(50, queueList.size());
            for (int i = 0; i < maxDisplay; i++) {
                sb.append(i + 1).append(". ").append(truncate(queueList.get(i).getInfo().title, MAX_TITLE_LENGTH)).append("\n");
            }

            int remaining = queueList.size() - 50;
            if (remaining > 0) {
                sb.append("... and ").append(remaining).append(" more song(s)");
            }
        }

        event.reply(sb.toString()).setEphemeral(true).queue();
    }

    private String truncate(String text, int maxLength) {
        if (text == null) return "Unknown Title";
        return text.length() > maxLength ? text.substring(0, maxLength - 3) + "..." : text;
    }

    void checkVoiceChannel(SlashCommandInteractionEvent event) {
        log.info("checkVoiceChannel called - isConnected: {} | userInChannel: {}", 
            voiceChannelService.isConnected(event.getGuild()),
            event.getMember().getVoiceState() != null && event.getMember().getVoiceState().inAudioChannel());
        
        if (!voiceChannelService.isConnected(event.getGuild())
                && !voiceChannelService.joinVoiceChannel(event.getMember())) {
            event.reply("You need to be in a voice channel first!").queue();
        }
    }

}
