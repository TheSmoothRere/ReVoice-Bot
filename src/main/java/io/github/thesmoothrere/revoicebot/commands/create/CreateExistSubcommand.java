package io.github.thesmoothrere.revoicebot.commands.create;

import io.github.thesmoothrere.revoicebot.command.SubSlashCommand;
import io.github.thesmoothrere.revoicebot.dto.ParentChannelDto;
import io.github.thesmoothrere.revoicebot.service.ParentChannelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.unions.GuildChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CreateExistSubcommand extends SubSlashCommand {
    private final ParentChannelService parentChannelService;

    @Override
    public void init() {
        setSubCommand("exist", "Create new parent channel from existing channel");
        addOption(OptionType.CHANNEL, "channel", "Channel to create from", true);
        addOption(OptionType.STRING, "prefix", "Prefix of the new channel", false);
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        GuildChannelUnion optionChannel = event.getOption("channel", null, OptionMapping::getAsChannel);
        String optionPrefix = event.getOption("prefix", "{user.name}", OptionMapping::getAsString);
        Guild guild = event.getGuild();
        assert guild != null; // assert guild because this command is guild only

        if (optionChannel instanceof VoiceChannel parentChannel) {
            Long parentChannelId = parentChannel.getIdLong();
            if (parentChannelService.isParentChannelExist(parentChannelId)) {
                event.reply("Parent channel already exists").setEphemeral(true).queue();
                return;
            }

            parentChannelService.saveParentChannel(
                    ParentChannelDto.builder()
                            .channelId(parentChannelId)
                            .guildId(guild.getIdLong())
                            .prefix(optionPrefix)
                            .build()
            );
            event.reply("Successfully created voice channel: " + parentChannel.getName()).setEphemeral(true).queue();
        } else {
            event.reply("Invalid channel provided").setEphemeral(true).queue();
        }
    }
}
