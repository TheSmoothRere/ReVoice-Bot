package io.github.thesmoothrere.revoicebot.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PrefixDto {
    private final String template;

    @NotNull(message = "Display name cannot be null")
    private final String displayName;

    @NotNull(message = "Number cannot be null")
    private final String number;

    @NotNull(message = "Alphabet cannot be null")
    private final String alphabet;
}
