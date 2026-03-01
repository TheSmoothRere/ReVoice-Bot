package dev.smootheez.dbt.command;

import jakarta.annotation.*;
import lombok.*;
import lombok.extern.slf4j.*;
import net.dv8tion.jda.api.events.interaction.command.*;
import net.dv8tion.jda.api.interactions.commands.build.*;

import java.util.*;

@Slf4j
@Getter
public abstract class SlashCommand implements ISlashCommand {
    private String name;
    private String description;
    private final Map<String, SubSlashCommand> subcommands;
    private final Map<String, GroupSlashCommand> subcommandGroups;

    protected SlashCommand() {
        this.subcommands = new HashMap<>();
        this.subcommandGroups = new HashMap<>();
    }

    @PostConstruct
    public abstract void init();

    public CommandData commandData() {
        nameAndDescriptionCheck(name, description);

        SlashCommandData data = Commands.slash(name, description);
        if (!subcommands.isEmpty()) {
            for (SubSlashCommand subcommand : subcommands.values()) {
                log.debug("Adding subcommand {} to command {}", subcommand.getName(), name);
                data.addSubcommands(subcommand.subcommandData());
            }
        }
        if (!subcommandGroups.isEmpty()) {
            for (GroupSlashCommand group : subcommandGroups.values()) {
                log.debug("Adding subcommand group {} to command {}", group.getName(), name);
                data.addSubcommandGroups(group.subcommandGroupData());
            }
        }
        return data;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        if (name == null || description == null)
            throw new IllegalStateException("Name and description must be set for SlashCommand: " + getClass().getName());

        String subcommandGroupName = event.getSubcommandGroup();
        if (subcommandGroupName != null) {
            GroupSlashCommand group = subcommandGroups.get(subcommandGroupName);
            if (group != null) {
                group.execute(event);
            } else {
                log.error("Unknown subcommand group: {} for command: {}", subcommandGroupName, name);
                event.reply("An unknown subcommand group was used.").setEphemeral(true).queue();
            }
            return;
        }

        String subcommandName = event.getSubcommandName();
        if (subcommandName != null) {
            SubSlashCommand subcommand = subcommands.get(subcommandName);
            if (subcommand != null) {
                subcommand.execute(event);
            } else {
                log.error("Unknown subcommand: {} for command: {}", subcommandName, name);
                event.reply("An unknown subcommand was used.").setEphemeral(true).queue();
            }
            return;
        }

        log.error("No subcommand or subcommand group found for command: {}", name);
        event.reply("This command requires a subcommand or subcommand group.").setEphemeral(true).queue();
    }

    protected SlashCommand setCommand(String name, String description) {
        this.name = name;
        this.description = description;
        return this;
    }

    public SlashCommand addSubcommand(SubSlashCommand subcommand) {
        this.subcommands.put(subcommand.getName(), subcommand);
        return this;
    }

    public SlashCommand addSubcommandGroup(GroupSlashCommand group) {
        this.subcommandGroups.put(group.getName(), group);
        return this;
    }
}
