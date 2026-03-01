package dev.smootheez.dbt.command;

import lombok.extern.slf4j.*;
import net.dv8tion.jda.api.events.interaction.command.*;
import org.springframework.stereotype.*;

import java.util.*;

@Slf4j
@Component
public class SlashCommandHandler {
    private final Map<String, SlashCommand> commands = new HashMap<>();

    public SlashCommandHandler(List<SlashCommand> slashCommands) {
        for (SlashCommand command : slashCommands) {
            commands.put(command.getName(), command);
            log.debug("Loaded slash command: {}", command.getName());
        }
    }

    public void handleSlashCommand(SlashCommandInteractionEvent event) {
        String commandName = event.getName();
        SlashCommand command = commands.get(commandName);

        if (command != null) {
            command.execute(event);
        } else {
            log.error("Unknown slash command: {}", commandName);
            event.reply("An unknown command was used.").setEphemeral(true).queue();
        }
    }
}
