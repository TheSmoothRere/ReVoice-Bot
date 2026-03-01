package dev.smootheez.dbt.config;

import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.boot.context.properties.*;
import org.springframework.validation.annotation.*;

@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "discord")
public class DiscordBotProperties {
    /**
     * Bot Token from Discord Developer Portal
     */
    @NotBlank(message = "Bot Token is required")
    @NotNull(message = "Bot Token is required")
    private String botToken;
}
