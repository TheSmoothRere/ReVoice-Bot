package io.github.thesmoothrere.revoicebot.commands.create;

import io.github.thesmoothrere.revoicebot.command.SubSlashCommand;
import io.github.thesmoothrere.revoicebot.dto.ParentChannelDto;
import io.github.thesmoothrere.revoicebot.service.ParentChannelService;
import io.github.thesmoothrere.revoicebot.util.OptionCommandNameUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.unions.GuildChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.requests.restaction.ChannelAction;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class CreateNewSubcommand extends SubSlashCommand {
    private final ParentChannelService parentChannelService;

    @Override
    public void init() {
        setSubCommand("new", "Create new parent channel");
        addOption(OptionType.STRING, OptionCommandNameUtil.NAME, "Name of the new channel", true);
        addOption(OptionType.STRING, OptionCommandNameUtil.PREFIX, "Prefix of the new channel", false);
        addOption(OptionType.CHANNEL, OptionCommandNameUtil.CATEGORY, "Category of the new channel", false);
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        String name = Objects.requireNonNull(event.getOption(OptionCommandNameUtil.NAME)).getAsString();
        String prefix = event.getOption(OptionCommandNameUtil.PREFIX, "{user.name}", OptionMapping::getAsString);
        GuildChannelUnion categoryOption = event.getOption(OptionCommandNameUtil.CATEGORY, null, OptionMapping::getAsChannel);

        Guild guild = Objects.requireNonNull(event.getGuild());

        log.debug("Attempting to create channel: {} with prefix: {} in category: {}", name, prefix, categoryOption);

        // Determine the action (Category vs Guild root)
        ChannelAction<VoiceChannel> action = (categoryOption instanceof Category category)
                ? category.createVoiceChannel(name)
                : guild.createVoiceChannel(name);

        action.queue(
                channel -> handleSuccess(event, channel, prefix),
                error -> handleFailure(event, error)
        );
    }

    private void handleSuccess(SlashCommandInteractionEvent event, VoiceChannel channel, String prefix) {
        saveParentToDb(channel, prefix);

        Category parentCategory = channel.getParentCategory();
        String categoryName = parentCategory != null ? parentCategory.getName() : "None";

        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("✅ Command Successful!")
                .setDescription("Successfully created voice channel")
                .addField("Parent Channel", channel.getAsMention(), false)
                .addField("Category", categoryName, false)
                .addField("Prefix", prefix, false)
                .setColor(Color.GREEN);

        event.replyEmbeds(embed.build()).setEphemeral(true).queue();
        log.debug("Successfully created and logged voice channel: {}", channel.getId());
    }

    private void handleFailure(SlashCommandInteractionEvent event, Throwable error) {
        log.error("Failed to create voice channel", error);

        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("❌ Command Failed!")
                .setDescription("Failed to create voice channel: " + error.getMessage())
                .setColor(Color.RED);

        event.replyEmbeds(embed.build()).setEphemeral(true).queue();
    }

    private void saveParentToDb(VoiceChannel channel, String prefix) {
        parentChannelService.saveParentChannel(
                ParentChannelDto.builder()
                        .channelId(channel.getIdLong())
                        .guildId(channel.getGuild().getIdLong())
                        .prefix(prefix)
                        .build()
        );
    }
}
