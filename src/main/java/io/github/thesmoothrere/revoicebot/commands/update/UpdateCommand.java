package io.github.thesmoothrere.revoicebot.commands.update;

import io.github.thesmoothrere.revoicebot.command.SlashCommand;
import io.github.thesmoothrere.revoicebot.commands.update.subcommand.UpdatePrefixSubcommand;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.interactions.InteractionContextType;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UpdateCommand extends SlashCommand {
    private final UpdatePrefixSubcommand updatePrefixSubcommand;

    @Override
    public void init() {
        setCommand("update", "update");
        addSubcommand(updatePrefixSubcommand);
        this.commandData().setContexts(InteractionContextType.GUILD);
    }
}
