package dev.smootheez.dbt.listener;

import dev.smootheez.dbt.command.*;
import lombok.*;
import lombok.extern.slf4j.*;
import net.dv8tion.jda.api.events.interaction.command.*;
import net.dv8tion.jda.api.hooks.*;
import org.springframework.stereotype.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class DiscordEventListener extends ListenerAdapter {
    private final SlashCommandHandler commandHandler;

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        log.debug("Received slash command: {}", event.getFullCommandName());
        commandHandler.handleSlashCommand(event);
    }
}
