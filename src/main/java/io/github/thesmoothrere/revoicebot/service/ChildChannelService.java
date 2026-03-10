package io.github.thesmoothrere.revoicebot.service;

import io.github.thesmoothrere.revoicebot.dto.ChildChannelDto;
import io.github.thesmoothrere.revoicebot.dto.PrefixDto;
import io.github.thesmoothrere.revoicebot.entity.ChildChannelEntity;
import io.github.thesmoothrere.revoicebot.repository.ChildChannelRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import org.jspecify.annotations.NonNull;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.EnumSet;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChildChannelService {
    private static final String REDIS_KEY_PREFIX = "channel:id:";

    private final RedisTemplate<String, Long> redisTemplate;
    private final ChildChannelRepository childChannelRepository;
    private final ParentChannelService parentChannelService;
    private final PrefixService prefixService;

    public void handleJoinedChannel(@NonNull VoiceChannel parentChannel, @NonNull Member member) {
        long parentId = parentChannel.getIdLong();

        // Verify this is a registered parent channel
        if (!parentChannelService.isParentChannelExist(parentId)) return;

        String memberName = member.getEffectiveName();
        log.debug("Member {} joined parent channel {}. Creating temporary channel.", memberName, parentId);

        // Create the channel with the correct name immediately
        long ownerId = member.getIdLong();
        PrefixDto prefixDto = new PrefixDto(parentChannelService.getPrefix(parentId));
        prefixDto.setDisplayName(memberName);
        String nextNumber = getNextNumber(parentId);
        prefixDto.setNumber(nextNumber);
        prefixDto.setAlphabet("A"); // TODO: use alphabet based on count
        parentChannel.createCopy()
                .addMemberPermissionOverride(
                        ownerId,
                        EnumSet.of(Permission.MANAGE_CHANNEL, Permission.VOICE_MOVE_OTHERS),
                        null
                )
                .setName(prefixService.resolvePrefix(prefixDto))
                .queue(tempChannel -> {
                    // Sequence: Move member -> Save to DB/Redis
                    tempChannel.getGuild().moveVoiceMember(member, tempChannel).queue();

                    persistChildChannel(
                            ChildChannelDto.builder()
                                    .channelId(tempChannel.getIdLong())
                                    .ownerId(ownerId)
                                    .count(nextNumber)
                                    .parentChannel(parentChannelService.getParentChannel(parentId))
                                    .build()
                    );

                    log.info("Created temporary channel {} for {}", tempChannel.getId(), memberName);
                }, throwable -> log.error("Failed to create temporary channel for {}", memberName, throwable));
    }

    public void handleLeftChannel(@NonNull VoiceChannel leftChannel) {
        long channelId = leftChannel.getIdLong();

        // Check if it's a managed child channel and is now empty
        if (isManagedChild(channelId) && leftChannel.getMembers().isEmpty()) {
            log.debug("Deleting empty temporary channel: {}", leftChannel.getName());

            leftChannel.delete().queue(
                    success -> clearMetadata(channelId),
                    error -> log.error("Could not delete channel {}", channelId, error)
            );
        }
    }

    private synchronized String getNextNumber(long parentId) {
        List<Integer> activeCounts = childChannelRepository.findActiveCounts(parentId);

        Integer nextAvailable = 1;
        for (Integer count : activeCounts) {
            if (count.equals(nextAvailable)) {
                nextAvailable++;
            } else {
                break;
            }
        }

        log.debug("Next available number for parent channel {}: {}", parentId, nextAvailable);
        return String.valueOf(nextAvailable);
    }

    private void persistChildChannel(ChildChannelDto childChannelDto) {
        long channelId = childChannelDto.getChannelId();

        // Save to Redis for fast lookup during voice state changes
        saveToCache(channelId);

        // Save to Database
        ChildChannelEntity entity = new ChildChannelEntity();
        entity.setChannelId(channelId);
        entity.setOwnerId(childChannelDto.getOwnerId());
        entity.setCount(childChannelDto.getCount());
        entity.setParentChannel(childChannelDto.getParentChannel());

        childChannelRepository.save(entity);
    }

    public void clearMetadata(long childChannelId) {
        log.debug("Clearing metadata for child channel: {}", childChannelId);
        removeFromCache(childChannelId);
        childChannelRepository.updateDeleteStatus(true, childChannelId);
    }

    private boolean isManagedChild(long channelId) {
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
