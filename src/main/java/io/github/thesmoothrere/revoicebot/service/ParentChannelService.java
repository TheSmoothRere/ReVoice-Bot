package io.github.thesmoothrere.revoicebot.service;

import io.github.thesmoothrere.revoicebot.dto.ParentChannelDto;
import io.github.thesmoothrere.revoicebot.entity.ParentChannelEntity;
import io.github.thesmoothrere.revoicebot.exception.ParentChannelNotFoundException;
import io.github.thesmoothrere.revoicebot.repository.ParentChannelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ParentChannelService {
    private final ParentChannelRepository parentChannelRepository;

    public Long getChannelId(Long channelId) {
        return parentChannelRepository.findByChannelId(channelId)
                .map(ParentChannelEntity::getChannelId).orElse(null);
    }

    public boolean isParentChannelExist(Long channelId) {
        return parentChannelRepository.existsByChannelId(channelId);
    }

    public ParentChannelEntity getParentChannel(Long channelId) {
        return parentChannelRepository.findByChannelIdAndDeletedFalse(channelId).orElseThrow(
                () -> throwParentChannelNotFounException(channelId)
        );
    }

    private static @NonNull ParentChannelNotFoundException throwParentChannelNotFounException(Long channelId) {
        return new ParentChannelNotFoundException("Parent channel not found for channel ID: " + channelId);
    }

    public ParentChannelEntity saveParentChannel(ParentChannelDto channelDto) {
        ParentChannelEntity entity = new ParentChannelEntity();
        entity.setChannelId(channelDto.getChannelId());
        entity.setGuildId(channelDto.getGuildId());
        entity.setPrefix(channelDto.getPrefix());
        return parentChannelRepository.save(entity);
    }
}
