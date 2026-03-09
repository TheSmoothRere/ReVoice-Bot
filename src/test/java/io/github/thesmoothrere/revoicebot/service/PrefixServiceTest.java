package io.github.thesmoothrere.revoicebot.service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PrefixServiceTest {
    private final PrefixService prefixService = new PrefixService();

    @Test
    void shouldResolvePlaceholders() {
        String template = "{user.name}'s Channel #{number} ({alphabet})";
        String result = prefixService.resolvePrefix(template, "Smooth", "1", "A");
        assertEquals("Smooth's Channel #1 (A)", result);
    }

    @Test
    void shouldReturnEmptyStringOnNullOrBlank() {
        assertEquals("", prefixService.resolvePrefix(null, "name", "1", "A"));
        assertEquals("", prefixService.resolvePrefix("  ", "name", "1", "A"));
    }

    @Test
    void shouldKeepUnknownPlaceholders() {
        String template = "Hello {unknown}";
        String result = prefixService.resolvePrefix(template, "name", "1", "A");
        assertEquals("Hello {unknown}", result);
    }

    @Test
    void shouldHandleMixedContent() {
        String template = "No placeholders here";
        assertEquals(template, prefixService.resolvePrefix(template, "name", "1", "A"));
    }
}
