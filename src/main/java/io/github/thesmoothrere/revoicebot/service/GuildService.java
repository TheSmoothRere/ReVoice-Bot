package io.github.thesmoothrere.revoicebot.service;

import io.github.thesmoothrere.revoicebot.entity.GuildEntity;
import io.github.thesmoothrere.revoicebot.repository.GuildRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class GuildService {
    private final GuildRepository guildRepository;

    @Transactional // Essential for data consistency
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

    public void updateDeleteStatus(Long guildId) {
        guildRepository.updateDeletedByGuildId(true, guildId);
    }
}
