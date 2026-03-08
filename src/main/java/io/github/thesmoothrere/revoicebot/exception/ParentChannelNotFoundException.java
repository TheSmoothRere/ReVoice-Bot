package io.github.thesmoothrere.revoicebot.exception;

public class ParentChannelNotFoundException extends RuntimeException {
    public ParentChannelNotFoundException(String message) {
        super(message);
    }
}
