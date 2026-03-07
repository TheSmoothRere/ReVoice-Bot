package io.github.thesmoothrere.revoicebot.service;

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
    private static final long PARENT_CHANNEL_ID = 1479853800272560248L; // TODO: later, should fetch from database instead
    private final RedisTemplate<Long, Object> redisTemplate;

    public void handleJoinedChannel(@NonNull VoiceChannel joinedChannel, Member member) {
        if (joinedChannel.getIdLong() != PARENT_CHANNEL_ID) return;

        log.debug("Member {} joined the parent channel. Creating a temporary voice channel.", member.getEffectiveName());
        joinedChannel.createCopy().queue(
                generatedVoice -> {
                    generatedVoice.getGuild().moveVoiceMember(member, generatedVoice).queue();
                    generatedVoice.getManager().setName(member.getEffectiveName()).queue();
                    saveValue(generatedVoice.getIdLong());
                },
                throwable -> log.error("Failed to create a temporary voice channel for member: {}",
                        member.getEffectiveName(), throwable)
        );
    }

    public void handleLeftChannel(@NonNull VoiceChannel leftChannel) {
        Long leftChannelId = leftChannel.getIdLong();

        if (leftChannelId.equals(getValue(leftChannelId)) && leftChannel.getMembers().isEmpty()) {
            log.debug("Deleting temporary voice channel: {}", leftChannel.getName());
            leftChannel.delete().queue(
                    success -> deleteValue(leftChannel.getIdLong()),
                    throwable -> log.error("Failed to delete temporary voice channel: {}", leftChannel.getName(), throwable)
            );
        }
    }

    private void saveValue(Long key) {
        log.debug("Saving key: {}", key);
        redisTemplate.opsForValue().set(key, key);
    }

    private Long getValue(Long key) {
        log.debug("Getting key: {}", key);
        return (Long) redisTemplate.opsForValue().get(key);
    }

    private void deleteValue(Long key) {
        log.debug("Deleting key: {}", key);
        redisTemplate.delete(key);
    }
}
