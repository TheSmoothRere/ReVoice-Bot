package io.github.thesmoothrere.revoicebot.listener;

import io.github.thesmoothrere.revoicebot.command.SlashCommandHandler;
import io.github.thesmoothrere.revoicebot.service.ChildChannelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DiscordEventListener extends ListenerAdapter {
    private final SlashCommandHandler commandHandler;
    private final ChildChannelService childChannelService;

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        log.debug("Received slash command: {}", event.getFullCommandName());
        commandHandler.handleSlashCommand(event);
    }

    @Override
    public void onGuildVoiceUpdate(GuildVoiceUpdateEvent event) {
        if (event.getChannelJoined() instanceof VoiceChannel voiceChannel) {
            childChannelService.handleJoinedChannel(voiceChannel, event.getMember());
        }
        if (event.getChannelLeft() instanceof VoiceChannel voiceChannel) {
            childChannelService.handleLeftChannel(voiceChannel);
        }
    }
}
