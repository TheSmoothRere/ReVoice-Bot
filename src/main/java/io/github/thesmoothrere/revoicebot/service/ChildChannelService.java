package io.github.thesmoothrere.revoicebot.service;

import io.github.thesmoothrere.revoicebot.dto.ChildChannelDto;
import io.github.thesmoothrere.revoicebot.entity.ChildChannelEntity;
import io.github.thesmoothrere.revoicebot.entity.ParentChannelEntity;
import io.github.thesmoothrere.revoicebot.exception.ParentChannelNotFoundException;
import io.github.thesmoothrere.revoicebot.repository.ChildChannelRepository;
import io.github.thesmoothrere.revoicebot.repository.ParentChannelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChildChannelService {
    private static final String REDIS_KEY_PREFIX = "channel:id:";

    private final RedisTemplate<String, Long> redisTemplate;
    private final ChildChannelRepository childChannelRepository;
    private final ParentChannelRepository parentChannelRepository;

    public String getAlphabetLabel(int number) {
        StringBuilder result = new StringBuilder();

        while (number > 0) {
            number--;
            char letter = (char) ('A' + (number % 26));
            result.insert(0, letter);
            number /= 26;
        }

        log.debug("Alphabet label for number {}: {}", number, result);
        return result.toString();
    }

    // Note: if someday need to sharding this bot. this method will not work anymore as intended
    public synchronized int getNextNumber(long parentId) {
        List<Integer> activeCounts = childChannelRepository.findActiveCounts(parentId);

        int nextAvailable = 1;
        for (int count : activeCounts) {
            if (count == nextAvailable) {
                nextAvailable++;
            } else {
                break;
            }
        }

        log.debug("Next available number for parent channel {}: {}", parentId, nextAvailable);
        return nextAvailable;
    }

    @Transactional
    public void persistChildChannel(ChildChannelDto childChannelDto) {
        long channelId = childChannelDto.getChannelId();

        // Save to Redis for fast lookup during voice state changes
        saveToCache(channelId);

        // Save to Database
        ChildChannelEntity entity = new ChildChannelEntity();
        entity.setChannelId(channelId);
        entity.setOwnerId(childChannelDto.getOwnerId());
        entity.setCount(childChannelDto.getCount());

        ParentChannelEntity parentChannel = parentChannelRepository.findByChannelId(childChannelDto.getParentChannelId()).orElseThrow(
                () -> new ParentChannelNotFoundException("Parent channel not found for channel ID: " + childChannelDto.getParentChannelId()
        ));
        entity.setParentChannel(parentChannel);

        childChannelRepository.save(entity);
    }

    public void clearMetadata(long childChannelId) {
        log.debug("Clearing metadata for child channel: {}", childChannelId);
        removeFromCache(childChannelId);
        childChannelRepository.updateDeleteStatus(true, childChannelId);
    }

    public boolean isManagedChild(long channelId) {
        Long cachedId = redisTemplate.opsForValue().get(getRedisKey(channelId));
        if (cachedId != null) return true;

        // Fallback to DB if Redis is empty/down
        return isChildChannelExist(channelId);
    }

    // --- Helper Methods ---

    private void saveToCache(long channelId) {
        redisTemplate.opsForValue().set(getRedisKey(channelId), channelId);
    }

    private void removeFromCache(long channelId) {
        redisTemplate.delete(getRedisKey(channelId));
    }

    private String getRedisKey(long channelId) {
        return REDIS_KEY_PREFIX + channelId;
    }

    public boolean isChildChannelExist(long channelId) {
        return childChannelRepository.existsByChannelId(channelId);
    }
}
