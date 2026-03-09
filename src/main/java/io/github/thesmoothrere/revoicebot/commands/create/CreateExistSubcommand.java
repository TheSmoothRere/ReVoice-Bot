package io.github.thesmoothrere.revoicebot.commands.create;

import io.github.thesmoothrere.revoicebot.command.SubSlashCommand;
import io.github.thesmoothrere.revoicebot.dto.ParentChannelDto;
import io.github.thesmoothrere.revoicebot.service.ParentChannelService;
import io.github.thesmoothrere.revoicebot.util.OptionCommandNameUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class CreateExistSubcommand extends SubSlashCommand {
    private final ParentChannelService parentChannelService;

    @Override
    public void init() {
        setSubCommand("exist", "Create new parent channel from existing channel");
        addOption(OptionType.CHANNEL, OptionCommandNameUtil.CHANNEL, "Channel to create from", true);
        addOption(OptionType.STRING, OptionCommandNameUtil.PREFIX, "Prefix of the new channel", false);
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        // 1. Extract and Validate inputs
        OptionMapping channelOption = Objects.requireNonNull(event.getOption(OptionCommandNameUtil.CHANNEL));
        String prefix = event.getOption(OptionCommandNameUtil.PREFIX, "{user.name}", OptionMapping::getAsString);
        Guild guild = Objects.requireNonNull(event.getGuild());

        // 2. Type Check (Early Return)
        if (!(channelOption.getAsChannel() instanceof VoiceChannel voiceChannel)) {
            replyError(event, "The selected channel is not a voice channel.");
            return;
        }

        long channelId = voiceChannel.getIdLong();

        // 3. Business Logic Check (Early Return)
        if (parentChannelService.isParentChannelExist(channelId)) {
            replyError(event, "This channel is already registered as a parent channel.");
            return;
        }

        // 4. Persistence
        saveToDatabase(voiceChannel, guild.getIdLong(), prefix);

        // 5. Success Response
        replySuccess(event, voiceChannel, prefix);
    }

    private void replySuccess(SlashCommandInteractionEvent event, VoiceChannel channel, String prefix) {
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("✅ Command Successful!")
                .setDescription("Successfully registered existing channel as a parent.")
                .addField("Parent Channel", channel.getAsMention(), false)
                .addField("Prefix", prefix, false)
                .setColor(Color.GREEN);

        event.replyEmbeds(embed.build()).setEphemeral(true).queue();
    }

    private void replyError(SlashCommandInteractionEvent event, String message) {
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("❌ Command Failed!")
                .setDescription(message)
                .setColor(Color.RED);

        event.replyEmbeds(embed.build()).setEphemeral(true).queue();
    }

    private void saveToDatabase(VoiceChannel channel, long guildId, String prefix) {
        parentChannelService.saveParentChannel(
                ParentChannelDto.builder()
                        .channelId(channel.getIdLong())
                        .guildId(guildId)
                        .prefix(prefix)
                        .build()
        );
    }
}
