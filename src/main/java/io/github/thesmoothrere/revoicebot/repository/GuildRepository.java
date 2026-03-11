package io.github.thesmoothrere.revoicebot.repository;

import io.github.thesmoothrere.revoicebot.entity.GuildEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface GuildRepository extends JpaRepository<GuildEntity, Long> {
    Optional<GuildEntity> findByGuildId(Long guildId);

    @Transactional
    @Modifying
    @Query("update GuildEntity g set g.deleted = ?1 where g.guildId = ?2")
    void updateDeletedByGuildId(Boolean deleted, Long guildId);
}
