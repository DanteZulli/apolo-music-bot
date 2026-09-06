package com.zullid.apolo_music_bot;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.RETURNS_SELF;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

/**
 * Smoke test that boots the full Spring context without touching the network.
 *
 * <p>The real {@link JDABuilder#build()} performs a login against Discord, so the
 * static factory is stubbed to return a mock {@link JDA} instead. This simulates
 * a successful authentication: no token and no network access required.
 */
@SpringBootTest(properties = "discord.bot.token=test-token")
class ApoloMusicBotApplicationTests {

    private static MockedStatic<JDABuilder> jdaBuilder;

    @Autowired
    private ApplicationContext context;

    @BeforeAll
    static void simulateSuccessfulDiscordAuth() {
        JDA jda = mock(JDA.class, RETURNS_DEEP_STUBS);
        JDABuilder builder = mock(JDABuilder.class, RETURNS_SELF);
        when(builder.build()).thenReturn(jda);

        jdaBuilder = mockStatic(JDABuilder.class);
        jdaBuilder.when(() -> JDABuilder.createDefault(anyString())).thenReturn(builder);
    }

    @AfterAll
    static void closeDiscordAuthMock() {
        jdaBuilder.close();
    }

    @Test
    void contextLoads() {
        assertThat(context.getBean(JDA.class)).isNotNull();
    }
}
