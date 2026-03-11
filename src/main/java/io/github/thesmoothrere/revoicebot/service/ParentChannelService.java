package io.github.thesmoothrere.revoicebot.service;

import io.github.thesmoothrere.revoicebot.dto.ParentChannelDto;
import io.github.thesmoothrere.revoicebot.dto.UpdatePrefixDto;
import io.github.thesmoothrere.revoicebot.entity.GuildEntity;
import io.github.thesmoothrere.revoicebot.entity.ParentChannelEntity;
import io.github.thesmoothrere.revoicebot.exception.GuildEntityNotFoundException;
import io.github.thesmoothrere.revoicebot.repository.GuildRepository;
import io.github.thesmoothrere.revoicebot.repository.ParentChannelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ParentChannelService {
    private final ParentChannelRepository parentChannelRepository;
    private final GuildRepository guildRepository;

    public String getPrefix(Long channelId) {
        return parentChannelRepository.findByChannelIdAndDeletedFalse(channelId)
                .map(ParentChannelEntity::getPrefix).orElse("No Prefix");
    }

    public void updatePrefix(UpdatePrefixDto updatePrefixDto) {
        parentChannelRepository.updatePrefix(updatePrefixDto.getPrefix(), updatePrefixDto.getChannelId());
    }

    public void removeParentChannel(Long channelId) {
        parentChannelRepository.updateDeleteStatus(true, channelId);
    }

    public boolean isParentChannelExist(Long channelId) {
        return parentChannelRepository.existsByChannelId(channelId);
    }

    @Transactional
    public ParentChannelEntity saveParentChannel(ParentChannelDto channelDto) {
        ParentChannelEntity entity = new ParentChannelEntity();
        entity.setChannelId(channelDto.getChannelId());
        entity.setPrefix(channelDto.getPrefix());

        GuildEntity guild = guildRepository.findByGuildId(channelDto.getGuildId()).orElseThrow(
                () -> new GuildEntityNotFoundException("Guild not found for guild ID: " + channelDto.getGuildId())
        );
        entity.setGuild(guild);
        return parentChannelRepository.save(entity);
    }
}
