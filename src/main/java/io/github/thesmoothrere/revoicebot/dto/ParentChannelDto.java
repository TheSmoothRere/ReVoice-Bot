package io.github.thesmoothrere.revoicebot.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ParentChannelDto {
    private Long guildId;
    private Long channelId;
    private String prefix;
}
