package io.github.thesmoothrere.revoicebot.service;

import io.github.thesmoothrere.revoicebot.dto.ParentChannelDto;
import io.github.thesmoothrere.revoicebot.dto.UpdatePrefixDto;
import io.github.thesmoothrere.revoicebot.entity.GuildEntity;
import io.github.thesmoothrere.revoicebot.entity.ParentChannelEntity;
import io.github.thesmoothrere.revoicebot.exception.GuildNotFoundException;
import io.github.thesmoothrere.revoicebot.repository.ChildChannelRepository;
import io.github.thesmoothrere.revoicebot.repository.GuildRepository;
import io.github.thesmoothrere.revoicebot.repository.ParentChannelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ParentChannelService {
    private final ParentChannelRepository parentChannelRepository;
    private final GuildRepository guildRepository;
    private final CacheManager cacheManager;
    private final ChildChannelRepository childChannelRepository;
    private final ChildChannelService childChannelService;

    @Cacheable(value = "channelPrefixes", key = "#channelId")
    public String getPrefix(Long channelId) {
        return parentChannelRepository.findByChannelIdAndDeletedFalse(channelId)
                .map(ParentChannelEntity::getPrefix).orElse("No Prefix");
    }

    @CacheEvict(value = "channelPrefixes", key = "#updatePrefixDto.channelId")
    public void updatePrefix(UpdatePrefixDto updatePrefixDto) {
        parentChannelRepository.updatePrefix(updatePrefixDto.getPrefix(), updatePrefixDto.getChannelId());
    }

    @Caching(evict = {
            @CacheEvict(value = "parentChannels", key = "#channelId"),
            @CacheEvict(value = "channelPrefixes", key = "#channelId")
    })
    public void removeParentChannel(Long channelId) {
        parentChannelRepository.updateDeleteStatus(true, channelId);
        childChannelRepository.getAllChildChannels(channelId).forEach(
                childChannel -> childChannelService.removeChildChannel(childChannel.getChannelId())
        );
    }

    // Manual evict because when join child channel and empty, the cache is no longer use
    public void resetParentChannelCache(Long channelId) {
        Cache cache = cacheManager.getCache("parentChannels");
        if (cache != null) {
            cache.evict(channelId);
            log.debug("Evicted channel {} from parentChannels cache", channelId);
        }
    }

    @Cacheable(value = "parentChannels", key = "#channelId")
    public boolean isParentChannelExist(Long channelId) {
        return parentChannelRepository.existsByChannelId(channelId);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "parentChannels", key = "#channelDto.channelId"),
            @CacheEvict(value = "channelPrefixes", key = "#channelDto.channelId")
    })
    public void saveParentChannel(ParentChannelDto channelDto) {
        ParentChannelEntity entity = new ParentChannelEntity();
        entity.setChannelId(channelDto.getChannelId());
        entity.setPrefix(channelDto.getPrefix());

        GuildEntity guild = guildRepository.findByGuildId(channelDto.getGuildId()).orElseThrow(
                () -> new GuildNotFoundException("Guild not found for guild ID: " + channelDto.getGuildId())
        );
        entity.setGuild(guild);
        parentChannelRepository.save(entity);
    }

    public long countParentChannels(Long guildId) {
        return parentChannelRepository.countByGuild_GuildIdAndDeletedFalse(guildId);
    }
}
