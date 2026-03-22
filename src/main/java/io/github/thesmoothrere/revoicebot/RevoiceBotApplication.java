package io.github.thesmoothrere.revoicebot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class RevoiceBotApplication {
    public static void main(String[] args) {
        SpringApplication.run(RevoiceBotApplication.class, args);
    }
}
