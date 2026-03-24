package io.github.thesmoothrere.revoicebot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BotPropertiesConfig {
    @Bean
    public DiscordBotProperties discordBotProperties() {
        return new DiscordBotProperties();
    }
}
