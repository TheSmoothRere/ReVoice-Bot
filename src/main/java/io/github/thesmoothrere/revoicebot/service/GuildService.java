package io.github.thesmoothrere.revoicebot.service;

import io.github.thesmoothrere.revoicebot.entity.GuildEntity;
import io.github.thesmoothrere.revoicebot.repository.ChildChannelRepository;
import io.github.thesmoothrere.revoicebot.repository.GuildRepository;
import io.github.thesmoothrere.revoicebot.repository.ParentChannelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class GuildService {
    private final GuildRepository guildRepository;
    private final ParentChannelRepository parentChannelRepository;
    private final ParentChannelService parentChannelService;

    @Transactional // Essential for data consistency
    @CacheEvict(value = "guilds", key = "#guildId")
    public void saveGuild(Long guildId) {
        // 1. Find by ID only (ignore the deleted flag for the search)
        GuildEntity entity = guildRepository.findByGuildId(guildId)
                .orElseGet(() -> {
                    log.debug("Creating brand new GuildEntity for guildId: {}", guildId);
                    GuildEntity newEntity = new GuildEntity();
                    newEntity.setGuildId(guildId);
                    return guildRepository.save(newEntity);
                });

        // 2. Only update and save if it was actually deleted or is brand new
        if (Boolean.TRUE.equals(entity.getDeleted())) {
            log.debug("Restoring GuildEntity for guildId: {}", guildId);
            entity.setDeleted(false);
            guildRepository.save(entity);
        }
    }

    @Cacheable(value = "guilds", key = "#guildId")
    public boolean isGuildExist(Long guildId) {
        return guildRepository.existsByGuildIdAndDeletedFalse(guildId);
    }

    @CacheEvict(value = "guilds", key = "#guildId")
    public void removeGuild(Long guildId) {
        guildRepository.updateDeletedByGuildId(true, guildId);
        parentChannelRepository.getAllParentChannels(guildId).forEach(
                parentChannel -> parentChannelService.removeParentChannel(parentChannel.getChannelId())
        );
    }
}
