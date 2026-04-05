package io.github.thesmoothrere.revoicebot.commands.create.subcommand;

import io.github.thesmoothrere.revoicebot.command.SubSlashCommand;
import io.github.thesmoothrere.revoicebot.helper.ChannelCommandHelper;
import io.github.thesmoothrere.revoicebot.service.ParentChannelService;
import io.github.thesmoothrere.revoicebot.util.OptionCommandNameUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class CreateExistSubcommand extends SubSlashCommand {
    private final ParentChannelService parentChannelService;
    private final ChannelCommandHelper commandHelper;

    @Override
    public void init() {
        setSubCommand("exist", "Create new parent channel from existing channel");
        addOption(OptionType.CHANNEL, OptionCommandNameUtil.CHANNEL, "Channel to create from", true);
        addOption(OptionType.STRING, OptionCommandNameUtil.PREFIX, "Prefix of the new channel", false);
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        OptionMapping channelOption = Objects.requireNonNull(event.getOption(OptionCommandNameUtil.CHANNEL));
        String prefix = event.getOption(OptionCommandNameUtil.PREFIX, "{user.name}'s Voice", OptionMapping::getAsString);
        Guild guild = Objects.requireNonNull(event.getGuild());

        if (commandHelper.checkLimitAndReply(guild.getIdLong(), event)) return;

        // 1. Type Check
        if (!(channelOption.getAsChannel() instanceof VoiceChannel voiceChannel)) {
            commandHelper.replyError(event, "The selected channel is not a voice channel.");
            return;
        }

        // 2. Permission Check (Best Practice)
        // Ensure bot can actually see and manage the channel it's supposed to monitor
        if (!guild.getSelfMember().hasPermission(voiceChannel, Permission.MANAGE_CHANNEL, Permission.VIEW_CHANNEL)) {
            commandHelper.replyError(event, "I need **View Channel** and **Manage Channels** permissions for " + voiceChannel.getAsMention());
            return;
        }

        // 3. Business Logic Check
        if (parentChannelService.isParentChannelExist(voiceChannel.getIdLong())) {
            commandHelper.replyError(event, "This channel is already registered as a parent channel.");
            return;
        }

        commandHelper.saveAndReplySuccess(event, voiceChannel, prefix);
    }
}
