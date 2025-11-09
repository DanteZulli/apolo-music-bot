package com.zullid.apolo_music_bot.listeners.events;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Component;

@Component
public class VoiceStateListener extends ListenerAdapter {

    @Override
    public void onGuildVoiceUpdate(GuildVoiceUpdateEvent event) {
        Member selfMember = event.getGuild().getSelfMember();
        if (event.getMember().equals(selfMember) && selfMember.getVoiceState() != null
                && selfMember.getVoiceState().inAudioChannel() && !selfMember.getVoiceState().isSelfDeafened()) {
            // If the bot is in a voice channel and is not deafened, deafen it.
            selfMember.deafen(true).queue();
        }
    }
}
