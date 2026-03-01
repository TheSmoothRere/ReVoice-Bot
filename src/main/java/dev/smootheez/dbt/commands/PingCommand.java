package dev.smootheez.dbt.commands;

import dev.smootheez.dbt.command.*;
import lombok.*;
import lombok.extern.slf4j.*;
import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.events.interaction.command.*;
import org.springframework.stereotype.*;

@Slf4j
@Component
public class PingCommand extends SlashCommand {

    @Override
    public void init() {
        setCommand("ping", "Pong!");
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        long latency = event.getJDA().getGatewayPing();
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Pong!" + " \uD83C\uDFD3");
        embedBuilder.addField("Latency", "`" + latency + "ms`", true);

        if (latency < 50) {
            embedBuilder.setColor(LatencyColor.LOW.colorCode);
        } else if (latency < 100) {
            embedBuilder.setColor(LatencyColor.MEDIUM.colorCode);
        } else {
            embedBuilder.setColor(LatencyColor.HIGH.colorCode);
        }

        event.replyEmbeds(embedBuilder.build()).queue();
    }

    @RequiredArgsConstructor
    enum LatencyColor {
        HIGH(0xFF0000),
        MEDIUM(0xFFFF00),
        LOW(0x00FF00);

        private final int colorCode;
    }
}
