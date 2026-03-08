package io.github.thesmoothrere.revoicebot.service;

import io.github.thesmoothrere.revoicebot.dto.ChildChannelDto;
import io.github.thesmoothrere.revoicebot.entity.ChildChannelEntity;
import io.github.thesmoothrere.revoicebot.repository.ChildChannelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import org.jspecify.annotations.NonNull;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChildChannelService {
    private final RedisTemplate<String, Long> redisTemplate;
    private final ChildChannelRepository childChannelRepository;
    private final ParentChannelService parentChannelService;

    public void handleJoinedChannel(@NonNull VoiceChannel joinedChannel, Member member) {
        Long joinedChannelId = joinedChannel.getIdLong();
        if (!joinedChannelId.equals(parentChannelService.getChannelId(joinedChannelId))) return;

        String memberEffectiveName = member.getEffectiveName();
        log.debug("Member {} joined the parent channel. Creating a temporary voice channel.", memberEffectiveName);
        joinedChannel.createCopy().queue(
                generatedVoice -> {
                    generatedVoice.getGuild().moveVoiceMember(member, generatedVoice).queue();
                    generatedVoice.getManager().setName(memberEffectiveName).queue();
                    saveValue(generatedVoice.getIdLong());
                    saveChildChannel(
                            ChildChannelDto.builder()
                                    .channelId(generatedVoice.getIdLong())
                                    .ownerId(member.getIdLong())
                                    .parentChannel(
                                            parentChannelService.getParentChannel(joinedChannelId)
                                    )
                                    .build()
                    );
                },
                throwable -> log.error("Failed to create a temporary voice channel for member: {}",
                        memberEffectiveName, throwable)
        );
    }

    public void handleLeftChannel(@NonNull VoiceChannel leftChannel) {
        Long leftChannelId = leftChannel.getIdLong();

        if (leftChannelId.equals(getValue(leftChannelId)) && leftChannel.getMembers().isEmpty()) {
            log.debug("Deleting temporary voice channel: {}", leftChannel.getName());
            leftChannel.delete().queue(
                    success -> clearMetadata(leftChannelId),
                    throwable -> log.error("Failed to delete temporary voice channel: {}", leftChannel.getName(), throwable)
            );
        }
    }

    public void clearMetadata(Long childChannelId) {
        deleteValue(childChannelId);
        removeChildChannel(childChannelId);
    }

    public void removeChildChannel(Long childChannelId) {
        childChannelRepository.updateDeleteStatus(true, childChannelId);
    }

    public boolean isChildChannelExist(Long childChannelId) {
        return childChannelRepository.existsByChannelId(childChannelId);
    }

    public ChildChannelEntity saveChildChannel(ChildChannelDto channelDto) {
        ChildChannelEntity entity = new ChildChannelEntity();
        entity.setChannelId(channelDto.getChannelId());
        entity.setOwnerId(channelDto.getOwnerId());
        entity.setParentChannel(channelDto.getParentChannel());
        return childChannelRepository.save(entity);
    }

    private void saveValue(Long value) {
        String redisKey = getRedisKey(value);
        log.debug("Saving key: {}", redisKey);
        redisTemplate.opsForValue().set(redisKey, value);
    }

    private static @NonNull String getRedisKey(Long value) {
        return "channel:id:" + value;
    }

    // TODO: add fallback to database if redis is down
    private Long getValue(Long value) {
        String redisKey = getRedisKey(value);
        log.debug("Getting key: {}", redisKey);
        return redisTemplate.opsForValue().get(redisKey);
    }

    private void deleteValue(Long value) {
        String redisKey = getRedisKey(value);
        log.debug("Deleting key: {}", redisKey);
        redisTemplate.delete(redisKey);
    }
}
