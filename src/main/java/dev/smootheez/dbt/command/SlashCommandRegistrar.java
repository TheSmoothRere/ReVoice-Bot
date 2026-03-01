package dev.smootheez.dbt.command;

import lombok.*;
import lombok.extern.slf4j.*;
import net.dv8tion.jda.api.*;
import org.springframework.stereotype.*;

import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class SlashCommandRegistrar {
    private final List<SlashCommand> slashCommands;

    public void registerSlashCommands(JDA jda) {
        if (slashCommands.isEmpty()) {
            log.warn("No slash commands found to register.");
            return;
        }

        log.info("Registering {} slash commands...", slashCommands.size());
        jda.updateCommands()
                .addCommands(slashCommands.stream().map(SlashCommand::commandData).toList())
                .queue(
                        success -> log.info("Successfully registered {} slash commands.", success.size()),
                        error -> log.error("Failed to register slash commands: {}", error.getMessage())
                );
    }
}
