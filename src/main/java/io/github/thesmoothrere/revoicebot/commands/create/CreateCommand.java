package io.github.thesmoothrere.revoicebot.commands.create;

import io.github.thesmoothrere.revoicebot.command.SlashCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.interactions.InteractionContextType;
import org.springframework.stereotype.Component;

@Slf4j
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
        this.commandData().setContexts(InteractionContextType.GUILD);
    }
}
