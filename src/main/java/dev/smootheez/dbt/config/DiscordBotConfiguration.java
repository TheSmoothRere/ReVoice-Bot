package dev.smootheez.dbt.config;

import dev.smootheez.dbt.command.*;
import dev.smootheez.dbt.listener.*;
import lombok.extern.slf4j.*;
import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.requests.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.context.annotation.*;

@Slf4j
@Configuration
public class DiscordBotConfiguration {
    /**
     * The bot token from the Discord Developer Portal.
     */
    @Value("${discord.bot-token}")
    private String botToken;

    /**
     * * Creates and configures the JDA (Java Discord API) instance.
     *
     * @return The configured JDA instance.
     * @throws InterruptedException If the bot's startup is interrupted.
     */
    @Bean
    public JDA jda(SlashCommandRegistrar slashCommandRegistrar, DiscordEventListener eventListener) throws InterruptedException {
        JDA jda = JDABuilder.createDefault(botToken)
                .enableIntents(
                        GatewayIntent.GUILD_MESSAGES,
                        GatewayIntent.GUILD_PRESENCES,
                        GatewayIntent.GUILD_MEMBERS
                )
                .addEventListeners(eventListener)
                .build();

        slashCommandRegistrar.registerSlashCommands(jda);
        jda.awaitReady();
        return jda;
    }
}
