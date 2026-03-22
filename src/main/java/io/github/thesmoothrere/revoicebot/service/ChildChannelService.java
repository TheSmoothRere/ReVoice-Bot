package io.github.thesmoothrere.revoicebot.service;

import io.github.thesmoothrere.revoicebot.dto.ChildChannelDto;
import io.github.thesmoothrere.revoicebot.entity.ChildChannelEntity;
import io.github.thesmoothrere.revoicebot.entity.ParentChannelEntity;
import io.github.thesmoothrere.revoicebot.exception.ParentChannelNotFoundException;
import io.github.thesmoothrere.revoicebot.repository.ChildChannelRepository;
import io.github.thesmoothrere.revoicebot.repository.ParentChannelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChildChannelService {
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
    @CacheEvict(value = "childChannels", key = "#childChannelDto.channelId")
    public void saveChildChannel(ChildChannelDto childChannelDto) {
        long channelId = childChannelDto.getChannelId();

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

    @CacheEvict(value = "childChannels", key = "#channelId")
    public void removeChildChannel(long channelId) {
        childChannelRepository.updateDeleteStatus(true, channelId);
    }

    @Cacheable(value = "childChannels", key = "#channelId")
    public boolean isChildChannelExist(long channelId) {
        return childChannelRepository.existsByChannelId(channelId);
    }
}
