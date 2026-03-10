package io.github.thesmoothrere.revoicebot.commands.create;

import io.github.thesmoothrere.revoicebot.command.SlashCommand;
import io.github.thesmoothrere.revoicebot.commands.create.subcommand.CreateExistSubcommand;
import io.github.thesmoothrere.revoicebot.commands.create.subcommand.CreateNewSubcommand;
import lombok.RequiredArgsConstructor;
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
        addSubcommand(createNewSubcommand); // need to limit the creation of parent channel to one per-guild for free tier
        addSubcommand(createExistSubcommand); // need to limit the creation of parent channel to one per-guild for free tier
        this.commandData().setContexts(InteractionContextType.GUILD);
    }
}
