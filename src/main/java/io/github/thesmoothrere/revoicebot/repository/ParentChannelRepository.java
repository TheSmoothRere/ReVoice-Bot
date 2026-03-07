package io.github.thesmoothrere.revoicebot.repository;

import io.github.thesmoothrere.revoicebot.entity.ParentChannelEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParentChannelRepository extends JpaRepository<ParentChannelEntity, Long> {
}
