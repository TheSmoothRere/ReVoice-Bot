package io.github.thesmoothrere.revoicebot.repository;

import io.github.thesmoothrere.revoicebot.entity.ChildChannelEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChildChannelRepository extends JpaRepository<ChildChannelEntity, Long> {
    boolean existsByChannelId(Long channelId);

    Optional<ChildChannelEntity> findByChannelId(Long channelId);

    @Transactional
    @Modifying
    @Query("update ChildChannelEntity c set c.deleted = ?1 where c.channelId = ?2")
    void updateDeleteStatus(Boolean deleted, Long channelId);

    @Query("""
            select c.count from ChildChannelEntity c
            where c.parentChannel.channelId = ?1 and c.deleted = false
            order by c.count""")
    List<Integer> findActiveCounts(Long channelId);

    @Query("select c from ChildChannelEntity c join fetch c.parentChannel p join fetch p.guild g where g.guildId = ?1 and c.deleted = false")
    List<ChildChannelEntity> getAllChildChannels(Long guildId);
}
