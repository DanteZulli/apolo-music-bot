package com.zullid.apolo_music_bot.config;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for {@link PlayerCommandConfig}.
 * <p>
 * Verifies that all seven global slash commands are registered with their expected names
 * and that both the success and failure callbacks complete without throwing, using mocked
 * JDA and command update actions without network access.
 * </p>
 *
 * @author Dante Zulli (dantezulli2004@gmail.com)
 */
@ExtendWith(MockitoExtension.class)
class PlayerCommandConfigTest {

    @Mock
    private JDA jda;

    private CommandListUpdateAction updateAction;

    @Captor
    private ArgumentCaptor<Consumer<List<Command>>> successCaptor;

    @Captor
    private ArgumentCaptor<Consumer<Throwable>> failureCaptor;

    private final List<CommandData> registeredCommands = new ArrayList<>();

    private PlayerCommandConfig config;

    /**
     * Creates the config with mocked JDA wiring before each test.
     */
    @BeforeEach
    void setUp() {
        updateAction = mock(CommandListUpdateAction.class, RETURNS_SELF);
        lenient().when(jda.updateCommands()).thenReturn(updateAction);
        lenient()
            .when(updateAction.addCommands(any(CommandData[].class)))
            .thenAnswer(invocation -> {
                for (Object arg : invocation.getArguments()) {
                    registeredCommands.add((CommandData) arg);
                }
                return updateAction;
            });
        config = new PlayerCommandConfig(jda);
    }

    /**
     * Tests that all music commands are registered with their expected names.
     * <p>
     * Given any application state, when commands are registered, then the seven slash
     * commands play, pause, resume, stop, skip, help and queue are sent to Discord.
     * </p>
     */
    @Test
    void registerCommands_registersAllSevenSlashCommands() {
        config.registerCommands();

        verify(updateAction).addCommands(any(CommandData[].class));
        Set<String> names = registeredCommands
            .stream()
            .map(CommandData::getName)
            .collect(Collectors.toSet());

        assertEquals(Set.of("play", "pause", "resume", "stop", "skip", "help", "queue"), names);
    }

    /**
     * Tests that both registration callbacks complete without throwing.
     * <p>
     * Given the queued registration action, when its success and failure callbacks fire,
     * then both complete (logging the outcome) without throwing.
     * </p>
     */
    @Test
    void registerCommands_callbacksCompleteWithoutThrowing() {
        config.registerCommands();

        verify(updateAction).queue(successCaptor.capture(), failureCaptor.capture());

        assertDoesNotThrow(() -> successCaptor.getValue().accept(List.of()));
        assertDoesNotThrow(() -> failureCaptor.getValue().accept(new RuntimeException("boom")));
    }
}
