package com.zullid.apolo_music_bot.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.zullid.apolo_music_bot.events.ReadyListener;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

@Configuration
public class DiscordConfig {

    @Value("${discord.bot.token}")
    private String token;

    @Bean
    JDA jdaBuilder(ReadyListener readyListener) {
        return JDABuilder.createDefault(token)
                .enableIntents(
                    GatewayIntent.GUILD_MESSAGES,
                    GatewayIntent.GUILD_MEMBERS,
                    GatewayIntent.MESSAGE_CONTENT,
                    GatewayIntent.GUILD_VOICE_STATES
                )
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .setChunkingFilter(ChunkingFilter.ALL)
                .addEventListeners(readyListener)
                .build();
    }

}
