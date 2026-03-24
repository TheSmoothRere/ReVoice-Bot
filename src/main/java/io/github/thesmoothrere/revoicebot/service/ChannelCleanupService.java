package io.github.thesmoothrere.revoicebot.service;

import io.github.thesmoothrere.revoicebot.entity.ChildChannelEntity;
import io.github.thesmoothrere.revoicebot.entity.ParentChannelEntity;
import io.github.thesmoothrere.revoicebot.repository.ChildChannelRepository;
import io.github.thesmoothrere.revoicebot.repository.ParentChannelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChannelCleanupService {
    private final JDA jda;
    private final ParentChannelRepository parentChannelRepository;
    private final ParentChannelService parentChannelService;
    private final ChildChannelRepository childChannelRepository;
    private final ChildChannelService childChannelService;

    @Scheduled(fixedRateString = "${discord.cleanup-interval:300000}")
    public void cleanupChannels() {
        log.debug("Starting channel cleanup task.");
        cleanupParentChannels();
        cleanupChildChannels();
        log.debug("Finished channel cleanup task.");
    }

    private void cleanupParentChannels() {
        List<ParentChannelEntity> activeParentChannels = parentChannelRepository.findAllActiveParentChannels();

        if (activeParentChannels.isEmpty()) return;

        log.debug("Checking {} active parent channels.", activeParentChannels.size());

        for (var parentChannel : activeParentChannels) {
            try {
                processParentChannel(parentChannel);
            } catch (Exception e) {
                log.error("Error checking parent channel {}", parentChannel.getChannelId(), e);
            }
        }
    }

    private void cleanupChildChannels() {
        var activeChildChannels = childChannelRepository.findAllActiveChildChannels();

        if (activeChildChannels.isEmpty()) return;

        log.debug("Checking {} active child channels.", activeChildChannels.size());

        for (var childChannel : activeChildChannels) {
            try {
                processChildChannel(childChannel);
            } catch (Exception e) {
                log.error("Error checking child channel {}", childChannel.getChannelId(), e);
            }
        }
    }

    private void processParentChannel(ParentChannelEntity parentChannel) {
        Long guildId = parentChannel.getGuild().getGuildId();
        Long channelId = parentChannel.getChannelId();

        Guild guild = jda.getGuildById(guildId);
        if (guild == null) {
            log.warn("Guild {} not found for parent channel {}. Marking as deleted.", guildId, channelId);
            parentChannelService.removeParentChannel(channelId);
            return;
        }

        VoiceChannel voiceChannel = guild.getVoiceChannelById(channelId);
        if (voiceChannel == null) {
            log.info("Parent Channel {} does not exist in guild {}. Marking as deleted.", channelId, guildId);
            parentChannelService.removeParentChannel(channelId);
        }
    }

    private void processChildChannel(ChildChannelEntity childChannel) {
        Long guildId = childChannel.getParentChannel().getGuild().getGuildId();
        Long channelId = childChannel.getChannelId();

        Guild guild = jda.getGuildById(guildId);
        if (guild == null) {
            log.warn("Guild {} not found for child channel {}. Marking as deleted.", guildId, channelId);
            childChannelService.removeChildChannel(channelId);
            return;
        }

        VoiceChannel voiceChannel = guild.getVoiceChannelById(channelId);
        if (voiceChannel == null) {
            log.info("Child Channel {} does not exist in guild {}. Marking as deleted.", channelId, guildId);
            childChannelService.removeChildChannel(channelId);
        } else if (voiceChannel.getMembers().isEmpty()) {
            log.info("Child Channel {} is empty. Deleting and marking as deleted.", channelId);
            voiceChannel.delete().queue(
                    _ -> childChannelService.removeChildChannel(channelId),
                    error -> log.error("Failed to delete empty child channel {}", channelId, error)
            );
        }
    }
}
