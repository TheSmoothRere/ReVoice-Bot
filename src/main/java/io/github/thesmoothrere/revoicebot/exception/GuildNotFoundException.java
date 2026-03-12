package io.github.thesmoothrere.revoicebot.exception;

public class GuildNotFoundException extends RuntimeException {
    public GuildNotFoundException(String message) {
        super(message);
    }
}
