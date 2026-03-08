package io.github.thesmoothrere.revoicebot.commands.create;

import io.github.thesmoothrere.revoicebot.command.SubSlashCommand;
import io.github.thesmoothrere.revoicebot.dto.ParentChannelDto;
import io.github.thesmoothrere.revoicebot.service.ParentChannelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.unions.GuildChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CreateNewSubcommand extends SubSlashCommand {
    private final ParentChannelService parentChannelService;

    @Override
    public void init() {
        setSubCommand("new", "Create new parent channel");
        addOption(OptionType.STRING, "name", "Name of the new channel", true);
        addOption(OptionType.STRING, "prefix", "Prefix of the new channel", false);
        addOption(OptionType.CHANNEL, "category", "Category of the new channel", false);
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        String optionName = event.getOption("name", "General", OptionMapping::getAsString);
        String optionPrefix = event.getOption("prefix", "{user.name}", OptionMapping::getAsString);
        GuildChannelUnion optionCategory = event.getOption("category", null, OptionMapping::getAsChannel);
        Guild guild = event.getGuild();
        assert guild != null; // assert guild because this command is guild only

        if (optionCategory instanceof Category category) {
            category.createVoiceChannel(optionName).queue(
                    parentChannel -> {
                        saveParentChannelToDatabase(parentChannel, guild, optionPrefix);
                        event.reply("Successfully created voice channel: " + parentChannel.getName()).setEphemeral(true).queue();
                    },
                    error -> {
                        log.error("Failed to create voice channel", error);
                        event.reply("Failed to create voice channel: " + error.getMessage()).setEphemeral(true).queue();
                    }
            );
        } else {
            guild.createVoiceChannel(optionName).queue(
                    parentChannel -> {
                        saveParentChannelToDatabase(parentChannel, guild, optionPrefix);
                        event.reply("Create voice channel with no category. Successfully created voice channel: " + parentChannel.getName()).setEphemeral(true).queue();
                    },
                    error -> {
                        log.error("Failed to create voice channel", error);
                        event.reply("Failed to create voice channel: " + error.getMessage()).setEphemeral(true).queue();
                    }
            );
        }
    }

    private void saveParentChannelToDatabase(VoiceChannel parentChannel, Guild guild, String optionPrefix) {
        parentChannelService.saveParentChannel(
                ParentChannelDto.builder()
                        .channelId(parentChannel.getIdLong())
                        .guildId(guild.getIdLong())
                        .prefix(optionPrefix)
                        .build()
        );
    }
}
