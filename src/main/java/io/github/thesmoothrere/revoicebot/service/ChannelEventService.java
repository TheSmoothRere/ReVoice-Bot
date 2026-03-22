package io.github.thesmoothrere.revoicebot.service;

import io.github.thesmoothrere.revoicebot.dto.ChildChannelDto;
import io.github.thesmoothrere.revoicebot.dto.PrefixDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.util.EnumSet;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChannelEventService {
    private final ParentChannelService parentChannelService;
    private final ChildChannelService childChannelService;
    private final PrefixService prefixService;

    public void handleJoinedChannel(@NonNull VoiceChannel parentChannel, @NonNull Member member) {
        long parentId = parentChannel.getIdLong();

        // Verify this is a registered parent channel
        if (!parentChannelService.isParentChannelExist(parentId)) return;

        String memberName = member.getEffectiveName();
        log.debug("Member {} joined parent channel {}. Creating temporary channel.", memberName, parentId);

        // Create the channel with the correct name immediately
        long ownerId = member.getIdLong();
        int nextNumber = childChannelService.getNextNumber(parentId);
        PrefixDto prefixDto = PrefixDto.builder()
                .template(parentChannelService.getPrefix(parentId))
                .displayName(memberName)
                .number(String.valueOf(nextNumber))
                .alphabet(childChannelService.getAlphabetLabel(nextNumber))
                .build();
        parentChannel.createCopy()
                .addMemberPermissionOverride(
                        ownerId,
                        EnumSet.of(
                                Permission.MANAGE_CHANNEL,
                                Permission.VOICE_MOVE_OTHERS
                        ),
                        null
                )
                .setName(prefixService.resolvePrefix(prefixDto))
                .queue(tempChannel -> {
                    // Sequence: Move member -> Save to DB/Redis
                    tempChannel.getGuild().moveVoiceMember(member, tempChannel).queue();

                    childChannelService.saveChildChannel(
                            ChildChannelDto.builder()
                                    .channelId(tempChannel.getIdLong())
                                    .ownerId(ownerId)
                                    .count(nextNumber)
                                    .parentChannelId(parentId)
                                    .build()
                    );

                    log.info("Created temporary channel {} for {}", tempChannel.getId(), memberName);
                }, throwable -> log.error("Failed to create temporary channel for {}", memberName, throwable));
    }

    public void handleLeftChannel(@NonNull VoiceChannel leftChannel) {
        long channelId = leftChannel.getIdLong();

        // Check if it's a managed child channel and is now empty
        if (childChannelService.isChildChannelExist(channelId) && leftChannel.getMembers().isEmpty()) {
            log.debug("Deleting empty temporary channel: {}", leftChannel.getName());

            leftChannel.delete().queue(
                    _ -> childChannelService.removeChildChannel(channelId),
                    error -> log.error("Could not delete channel {}", channelId, error)
            );
        }
    }
}
