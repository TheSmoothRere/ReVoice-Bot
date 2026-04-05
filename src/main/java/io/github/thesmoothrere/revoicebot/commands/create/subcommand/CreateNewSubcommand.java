package io.github.thesmoothrere.revoicebot.commands.create.subcommand;

import io.github.thesmoothrere.revoicebot.command.SubSlashCommand;
import io.github.thesmoothrere.revoicebot.helper.ChannelCommandHelper;
import io.github.thesmoothrere.revoicebot.util.OptionCommandNameUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.unions.GuildChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.requests.restaction.ChannelAction;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class CreateNewSubcommand extends SubSlashCommand {
    private final ChannelCommandHelper commandHelper;

    @Override
    public void init() {
        setSubCommand("new", "Create new parent channel");
        addOption(OptionType.STRING, OptionCommandNameUtil.NAME, "Name of the new channel", true);
        addOption(OptionType.STRING, OptionCommandNameUtil.PREFIX, "Prefix of the new channel", false);
        addOption(OptionType.CHANNEL, OptionCommandNameUtil.CATEGORY, "Category of the new channel", false);
    }

    private static @NonNull ChannelAction<VoiceChannel> createVoiceChannel(GuildChannelUnion categoryOption, String name, Guild guild) {
        return (categoryOption instanceof Category category)
                ? category.createVoiceChannel(name)
                : guild.createVoiceChannel(name);
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        String name = Objects.requireNonNull(event.getOption(OptionCommandNameUtil.NAME)).getAsString();
        String prefix = event.getOption(OptionCommandNameUtil.PREFIX, "{user.name}'s Voice", OptionMapping::getAsString);
        GuildChannelUnion categoryOption = event.getOption(OptionCommandNameUtil.CATEGORY, null, OptionMapping::getAsChannel);

        Guild guild = Objects.requireNonNull(event.getGuild());

        // 1. Check DB limits
        if (commandHelper.checkLimitAndReply(guild.getIdLong(), event)) return;

        // 2. Permission Check (Crucial Fix)
        if (!hasManageChannelPermission(guild, categoryOption)) {
            commandHelper.replyError(event, "I lack the **Manage Channels** permission to create a channel here.");
            return;
        }

        log.debug("Attempting to create channel: {} with prefix: {} in category: {}", name, prefix, categoryOption);

        createVoiceChannel(categoryOption, name, guild).queue(
                channel -> commandHelper.saveAndReplySuccess(event, channel, prefix),
                error -> replyError(event, "Failed to create voice channel: " + error.getMessage(), error)
        );
    }

    private boolean hasManageChannelPermission(Guild guild, GuildChannelUnion categoryOption) {
        Member self = guild.getSelfMember();
        if (categoryOption instanceof Category category) {
            return self.hasPermission(category, Permission.MANAGE_CHANNEL);
        }
        return self.hasPermission(Permission.MANAGE_CHANNEL);
    }

    private void replyError(SlashCommandInteractionEvent event, String description, Throwable error) {
        log.error("Failed to create voice channel. Error: ", error);
        commandHelper.replyError(event, description);
    }
}
