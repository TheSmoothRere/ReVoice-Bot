package io.github.thesmoothrere.revoicebot.repository;

import io.github.thesmoothrere.revoicebot.entity.ParentChannelEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ParentChannelRepository extends JpaRepository<ParentChannelEntity, Long> {
    Optional<ParentChannelEntity> findByChannelId(Long channelId);
    Optional<ParentChannelEntity> findByChannelIdAndDeletedFalse(Long channelId);

    boolean existsByChannelId(Long channelId);
}
