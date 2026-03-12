package io.github.thesmoothrere.revoicebot.config;

import io.github.thesmoothrere.revoicebot.command.*;
import io.github.thesmoothrere.revoicebot.listener.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.*;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;

@Slf4j
@Configuration
@RequiredArgsConstructor
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
                        GatewayIntent.GUILD_MEMBERS,
                        GatewayIntent.GUILD_VOICE_STATES
                ).enableCache(
                        CacheFlag.VOICE_STATE
                )
                .setStatus(OnlineStatus.IDLE)
                .setActivity(Activity.customStatus("Create temp voice for you"))
                .addEventListeners(eventListener)
                .build();

        slashCommandRegistrar.registerSlashCommands(jda);
        jda.awaitReady();
        return jda;
    }
}
