package io.github.thesmoothrere.revoicebot.commands.create.subcommand;

import io.github.thesmoothrere.revoicebot.command.SubSlashCommand;
import io.github.thesmoothrere.revoicebot.helper.ParentChannelCommandHelper;
import io.github.thesmoothrere.revoicebot.service.ParentChannelService;
import io.github.thesmoothrere.revoicebot.util.OptionCommandNameUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.unions.GuildChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.requests.restaction.ChannelAction;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class CreateNewSubcommand extends SubSlashCommand {
    private final ParentChannelCommandHelper commandHelper;

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
        String prefix = event.getOption(OptionCommandNameUtil.PREFIX, "{user.name}'s Voice", OptionMapping::getAsString);
        GuildChannelUnion categoryOption = event.getOption(OptionCommandNameUtil.CATEGORY, null, OptionMapping::getAsChannel);

        Guild guild = Objects.requireNonNull(event.getGuild());

        if (commandHelper.checkLimitAndReply(guild.getIdLong(), event)) return;

        log.debug("Attempting to create channel: {} with prefix: {} in category: {}", name, prefix, categoryOption);

        // Determine the action (Category vs Guild root)
        ChannelAction<VoiceChannel> action = (categoryOption instanceof Category category)
                ? category.createVoiceChannel(name)
                : guild.createVoiceChannel(name);

        action.queue(
                channel -> commandHelper.saveAndReplySuccess(event, channel, prefix),
                error -> replyError(event, "Failed to create voice channel: " + error.getMessage(), error)
        );
    }

    private void replyError(SlashCommandInteractionEvent event, String description, Throwable error) {
        log.error("Failed to create voice channel. Error: ", error);
        commandHelper.replyError(event, description);
    }
}
