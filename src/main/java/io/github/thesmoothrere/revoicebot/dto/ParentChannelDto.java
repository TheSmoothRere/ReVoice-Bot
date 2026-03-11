package io.github.thesmoothrere.revoicebot.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ParentChannelDto {
    @NotNull(message = "Channel ID cannot be null")
    private Long channelId;

    @NotNull(message = "Prefix cannot be null")
    private String prefix;

    @NotNull(message = "Guild ID cannot be null")
    private Long guildId;
}
