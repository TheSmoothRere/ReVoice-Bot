package io.github.thesmoothrere.revoicebot.commands;

import io.github.thesmoothrere.revoicebot.command.SlashCommand;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class HelpCommand extends SlashCommand {
    @Override
    public void init() {
        setCommand("help", "Get some help");
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        EmbedBuilder replyMessage = new EmbedBuilder();
        replyMessage.setTitle("ReVoice Bot Help")
                .setDescription("ReVoice helps you manage temporary voice channels automatically.")
                .addField("Commands",
                        """
                                **/create new** - Create a new parent voice channel.
                                **/create exist** - Set an existing voice channel as a parent.
                                **/update prefix** - Change the name prefix for temporary channels.
                                **/ping** - Check the bot's latency.""", false)
                .addField("How it works",
                        "When a user joins a **Parent Channel**, the bot creates a new temporary voice channel and moves the user into it. " +
                        "Once the temporary channel is empty, it is automatically deleted.", false)
                .setColor(0x3498db);

        log.debug("Help command executed by user: {}", event.getUser().getName());
        event.replyEmbeds(replyMessage.build()).queue();
    }
}
