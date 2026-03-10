package io.github.thesmoothrere.revoicebot.repository;

import io.github.thesmoothrere.revoicebot.entity.ParentChannelEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface ParentChannelRepository extends JpaRepository<ParentChannelEntity, Long> {
    Optional<ParentChannelEntity> findByChannelId(Long channelId);
    Optional<ParentChannelEntity> findByChannelIdAndDeletedFalse(Long channelId);

    boolean existsByChannelId(Long channelId);

    @Transactional
    @Modifying
    @Query("update ParentChannelEntity p set p.deleted = ?1 where p.channelId = ?2")
    void updateDeleteStatus(Boolean deleted, Long channelId);

    @Transactional
    @Modifying
    @Query("update ParentChannelEntity p set p.prefix = ?1 where p.channelId = ?2")
    void updatePrefix(String prefix, Long channelId);
}
