package io.github.thesmoothrere.revoicebot.commands.update;

import io.github.thesmoothrere.revoicebot.command.SubSlashCommand;
import io.github.thesmoothrere.revoicebot.dto.UpdatePrefixDto;
import io.github.thesmoothrere.revoicebot.service.ParentChannelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.channel.unions.GuildChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UpdatePrefixSubcommand extends SubSlashCommand {
    private final ParentChannelService parentChannelService;

    @Override
    public void init() {
        setSubCommand("prefix", "Update prefix");
        addOption(OptionType.CHANNEL, "channel", "Channel to update prefix", true);
        addOption(OptionType.STRING, "prefix", "New prefix for the channel", true);
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        OptionMapping optionChannel = event.getOption("channel");
        assert optionChannel != null; // assert because it's required option
        GuildChannelUnion selectedChannel = optionChannel.getAsChannel();
        OptionMapping optionPrefix = event.getOption("prefix");
        assert optionPrefix != null; // assert because it's required option
        String newPrefix = optionPrefix.getAsString();

        long channelId = selectedChannel.getIdLong();
        if (parentChannelService.isParentChannelExist(channelId)) {
            parentChannelService.updatePrefix(
                    UpdatePrefixDto.builder()
                            .prefix(newPrefix)
                            .channelId(channelId)
                            .build()
            );
            event.reply("Successfully updated prefix for channel: " + selectedChannel.getName()).setEphemeral(true).queue();
        } else {
            event.reply("The selected channel is not a registered parent channel.").setEphemeral(true).queue();
        }
    }
}
