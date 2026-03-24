package io.github.thesmoothrere.revoicebot.service;

import io.github.thesmoothrere.revoicebot.entity.GuildEntity;
import io.github.thesmoothrere.revoicebot.repository.GuildRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GuildCleanupService {
    private final JDA jda;
    private final GuildRepository guildRepository;
    private final GuildService guildService;

    @Scheduled(fixedRateString = "${discord.cleanup-interval:300000}")
    public void cleanupGuilds() {
        log.debug("Starting guild cleanup task.");
        List<GuildEntity> activeGuilds = guildRepository.findAllActiveGuilds();

        if (activeGuilds.isEmpty()) return;

        log.debug("Checking {} active guilds.", activeGuilds.size());

        for (var guildEntity : activeGuilds) {
            try {
                processGuild(guildEntity);
            } catch (Exception e) {
                log.error("Error checking guild {}", guildEntity.getGuildId(), e);
            }
        }
        log.debug("Finished guild cleanup task.");
    }

    private void processGuild(GuildEntity guildEntity) {
        Long guildId = guildEntity.getGuildId();
        Guild guild = jda.getGuildById(guildId);

        if (guild == null) {
            log.warn("Guild {} not found in JDA. Marking as deleted.", guildId);
            guildService.removeGuild(guildId);
        }
    }
}
