package dev.smootheez.dbt.command;

import net.dv8tion.jda.api.events.interaction.command.*;

public interface ISlashCommand {
    void init();

    void execute(SlashCommandInteractionEvent event);

    default void nameAndDescriptionCheck(String name, String description) {
        if (name == null || description == null)
            throw new IllegalStateException("Name and description must be set for SlashCommand: " + getClass().getName());
    }
}
