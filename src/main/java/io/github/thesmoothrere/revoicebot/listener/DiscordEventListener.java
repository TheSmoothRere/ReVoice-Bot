package io.github.thesmoothrere.revoicebot.listener;

import io.github.thesmoothrere.revoicebot.command.SlashCommandHandler;
import io.github.thesmoothrere.revoicebot.service.ChannelEventService;
import io.github.thesmoothrere.revoicebot.service.ChildChannelService;
import io.github.thesmoothrere.revoicebot.service.GuildService;
import io.github.thesmoothrere.revoicebot.service.ParentChannelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.channel.ChannelDeleteEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DiscordEventListener extends ListenerAdapter {
    private final SlashCommandHandler commandHandler;
    private final ChannelEventService channelEventService;
    private final ChildChannelService childChannelService;
    private final ParentChannelService parentChannelService;
    private final GuildService guildService;

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        log.debug("Received slash command: {}", event.getFullCommandName());
        commandHandler.handleSlashCommand(event);
    }

    @Override
    public void onGuildVoiceUpdate(GuildVoiceUpdateEvent event) {
        if (!guildService.isGuildExist(event.getGuild().getIdLong())) return;
        if (event.getChannelJoined() instanceof VoiceChannel voiceChannel) {
            channelEventService.handleJoinedChannel(voiceChannel, event.getMember());
        }
        if (event.getChannelLeft() instanceof VoiceChannel voiceChannel) {
            channelEventService.handleLeftChannel(voiceChannel);
        }
    }

    @Override
    public void onChannelDelete(ChannelDeleteEvent event) {
        if (event.getChannel() instanceof VoiceChannel voiceChannel) {
            long channelId = voiceChannel.getIdLong();
            parentChannelService.resetParentChannelCache(channelId);

            parentChannelService.removeParentChannel(channelId);
            childChannelService.removeChildChannel(channelId);
        }
    }

    @Override
    public void onGuildJoin(GuildJoinEvent event) {
        guildService.saveGuild(event.getGuild().getIdLong());
    }

    @Override
    public void onGuildLeave(GuildLeaveEvent event) {
        guildService.removeGuild(event.getGuild().getIdLong());
    }

    @Override
    public void onGuildReady(GuildReadyEvent event) {
        guildService.saveGuild(event.getGuild().getIdLong());
    }
}
