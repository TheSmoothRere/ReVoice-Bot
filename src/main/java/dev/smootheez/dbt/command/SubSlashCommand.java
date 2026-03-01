package dev.smootheez.dbt.command;

import jakarta.annotation.*;
import lombok.*;
import lombok.extern.slf4j.*;
import net.dv8tion.jda.api.interactions.commands.*;
import net.dv8tion.jda.api.interactions.commands.build.*;

import java.util.*;

@Slf4j
@Getter
public abstract class SubSlashCommand implements ISlashCommand {
    private String name;
    private String description;
    private final List<OptionData> options;

    protected SubSlashCommand() {
        this.options = new ArrayList<>();
    }

    @PostConstruct
    public abstract void init();

    public SubcommandData subcommandData() {
        nameAndDescriptionCheck(name, description);

        SubcommandData data = new SubcommandData(name, description);
        if (!options.isEmpty()) {
            log.debug("Adding {} options to subcommand: {}", options.size(), name);
            data.addOptions(options);
        }
        return data;
    }

    protected SubSlashCommand setSubCommand(String name, String description) {
        this.name = name;
        this.description = description;
        return this;
    }

    public SubSlashCommand addOption(OptionType type, String name, String description, boolean required) {
        this.options.add(new OptionData(type, name, description, required));
        return this;
    }
}
