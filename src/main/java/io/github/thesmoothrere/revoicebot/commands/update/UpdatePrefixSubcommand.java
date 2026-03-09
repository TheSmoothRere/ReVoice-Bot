package io.github.thesmoothrere.revoicebot.commands.update;

import io.github.thesmoothrere.revoicebot.command.SubSlashCommand;
import io.github.thesmoothrere.revoicebot.dto.UpdatePrefixDto;
import io.github.thesmoothrere.revoicebot.service.ParentChannelService;
import io.github.thesmoothrere.revoicebot.util.OptionCommandNameUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class UpdatePrefixSubcommand extends SubSlashCommand {
    private final ParentChannelService parentChannelService;

    @Override
    public void init() {
        setSubCommand("prefix", "Update prefix for a parent channel");
        addOption(OptionType.CHANNEL, OptionCommandNameUtil.CHANNEL, "Channel to update", true);
        addOption(OptionType.STRING, OptionCommandNameUtil.PREFIX, "New prefix for the channel", true);
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        // 1. Extract and Validate
        OptionMapping channelOption = Objects.requireNonNull(event.getOption(OptionCommandNameUtil.CHANNEL));
        OptionMapping prefixOption = Objects.requireNonNull(event.getOption(OptionCommandNameUtil.PREFIX));

        long channelId = channelOption.getAsChannel().getIdLong();
        String newPrefix = prefixOption.getAsString();

        // 2. Business Logic Check (Guard Clause)
        if (!parentChannelService.isParentChannelExist(channelId)) {
            replyError(event);
            return;
        }

        // 3. Update Persistence
        parentChannelService.updatePrefix(
                UpdatePrefixDto.builder()
                        .channelId(channelId)
                        .prefix(newPrefix)
                        .build()
        );

        // 4. Success Response
        replySuccess(event, channelOption.getAsChannel(), newPrefix);
    }

    private void replySuccess(SlashCommandInteractionEvent event, GuildChannel channel, String newPrefix) {
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("✅ Prefix Updated")
                .setDescription("Successfully updated the configuration for this channel.")
                .addField("Channel", channel.getAsMention(), false)
                .addField("New Prefix", "`" + newPrefix + "`", false)
                .setColor(Color.GREEN);

        event.replyEmbeds(embed.build()).setEphemeral(true).queue();
        log.info("Updated prefix to '{}' for channel {}", newPrefix, channel.getId());
    }

    private void replyError(SlashCommandInteractionEvent event) {
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("❌ Update Failed")
                .setDescription("The selected channel is not a registered parent channel.")
                .setColor(Color.RED);

        event.replyEmbeds(embed.build()).setEphemeral(true).queue();
    }
}
