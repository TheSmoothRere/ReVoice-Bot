package io.github.thesmoothrere.revoicebot.repository;

import io.github.thesmoothrere.revoicebot.entity.ChildChannelEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface ChildChannelRepository extends JpaRepository<ChildChannelEntity, Long> {
    Optional<ChildChannelEntity> findByChannelIdAndDeletedFalse(Long channelId);

    @Transactional
    @Modifying
    @Query("update ChildChannelEntity c set c.deleted = ?1 where c.channelId = ?2")
    void updateDeleteStatus(Boolean deleted, Long channelId);
}
