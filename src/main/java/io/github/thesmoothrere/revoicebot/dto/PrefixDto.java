package io.github.thesmoothrere.revoicebot.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class PrefixDto {
    private final String template;

    @NotNull(message = "Display name cannot be null")
    private String displayName;

    @NotNull(message = "Number cannot be null")
    private String number;

    @NotNull(message = "Alphabet cannot be null")
    private String alphabet;
}
