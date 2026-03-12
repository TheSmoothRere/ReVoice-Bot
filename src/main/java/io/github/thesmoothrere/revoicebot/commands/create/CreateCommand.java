package io.github.thesmoothrere.revoicebot.commands.create;

import io.github.thesmoothrere.revoicebot.command.SlashCommand;
import io.github.thesmoothrere.revoicebot.commands.create.subcommand.CreateExistSubcommand;
import io.github.thesmoothrere.revoicebot.commands.create.subcommand.CreateNewSubcommand;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.InteractionContextType;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CreateCommand extends SlashCommand {
    private final CreateNewSubcommand createNewSubcommand;
    private final CreateExistSubcommand createExistSubcommand;

    @Override
    public void init() {
        setCommand("create", "create");
        addSubcommand(createNewSubcommand);
        addSubcommand(createExistSubcommand);
    }

    @Override
    public CommandData commandData() {
        return super.commandData().setContexts(InteractionContextType.GUILD);
    }
}
