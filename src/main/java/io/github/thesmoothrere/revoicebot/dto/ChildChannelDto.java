package io.github.thesmoothrere.revoicebot.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ChildChannelDto {
    private Long channelId;
    private Long ownerId;
    private String count;
    private Long parentChannelId;
}
