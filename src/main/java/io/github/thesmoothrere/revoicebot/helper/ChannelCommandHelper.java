package io.github.thesmoothrere.revoicebot.helper;

import io.github.thesmoothrere.revoicebot.dto.ParentChannelDto;
import io.github.thesmoothrere.revoicebot.service.ParentChannelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Component;

import java.awt.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChannelCommandHelper {
    private final ParentChannelService parentChannelService;

    public boolean checkLimitAndReply(long guildId, SlashCommandInteractionEvent event) {
        if (parentChannelService.countParentChannels(guildId) >= 1) {
            replyError(event, "You can only have one parent channel per guild.");
            return true;
        }
        return false;
    }

    public void saveAndReplySuccess(SlashCommandInteractionEvent event, VoiceChannel channel, String prefix) {
        parentChannelService.saveParentChannel(ParentChannelDto.builder()
                .channelId(channel.getIdLong())
                .guildId(channel.getGuild().getIdLong())
                .prefix(prefix)
                .build());

        Category category = channel.getParentCategory();
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("✅ Command Successful!")
                .setDescription("Successfully registered parent channel.")
                .addField("Channel", channel.getAsMention(), true)
                .addField("Prefix", "`" + prefix + "`", true)
                .addField("Category", category != null ? category.getName() : "None", true)
                .setColor(Color.GREEN);

        log.debug("Saved parent channel: {} with prefix: {} in guild: {}", channel.getId(), prefix, channel.getGuild().getId());
        event.replyEmbeds(embed.build()).queue();
    }

    public void replyError(SlashCommandInteractionEvent event, String message) {
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("❌ Command Failed!")
                .setDescription(message)
                .setColor(Color.RED);

        log.error("Failed creating parent channel. Description: {}", message);
        // If the interaction is already acknowledged (e.g. in a callback), use hook
        if (event.isAcknowledged()) {
            event.getHook().sendMessageEmbeds(embed.build()).queue();
        } else {
            event.replyEmbeds(embed.build()).queue();
        }
    }
}
