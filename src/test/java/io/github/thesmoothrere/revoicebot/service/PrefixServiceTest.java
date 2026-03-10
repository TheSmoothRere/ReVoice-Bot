package io.github.thesmoothrere.revoicebot.service;

import io.github.thesmoothrere.revoicebot.dto.PrefixDto;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PrefixServiceTest {
    private final PrefixService prefixService = new PrefixService();

    @Test
    void shouldResolvePlaceholders() {
        String template = "{user.name}'s Channel #{number} ({alphabet})";
        PrefixDto prefixDto = new PrefixDto(template);
        prefixDto.setDisplayName("Smooth");
        prefixDto.setNumber("1");
        prefixDto.setAlphabet("A");
        String result = prefixService.resolvePrefix(prefixDto);
        assertEquals("Smooth's Channel #1 (A)", result);
    }

    @Test
    void shouldReturnEmptyStringOnNullOrBlank() {
        PrefixDto prefixDto = new PrefixDto(null);
        prefixDto.setDisplayName("name");
        prefixDto.setNumber("1");
        prefixDto.setAlphabet("A");
        assertEquals("Generated Voice", prefixService.resolvePrefix(prefixDto));

        PrefixDto prefixDto1 = new PrefixDto("   ");
        prefixDto1.setDisplayName("name");
        prefixDto1.setNumber("1");
        prefixDto1.setAlphabet("A");
        assertEquals("Generated Voice", prefixService.resolvePrefix(prefixDto1));
    }

    @Test
    void shouldKeepUnknownPlaceholders() {
        String template = "Hello {unknown}";
        PrefixDto prefixDto = new PrefixDto(template);
        prefixDto.setDisplayName("name");
        prefixDto.setNumber("1");
        prefixDto.setAlphabet("A");
        String result = prefixService.resolvePrefix(prefixDto);
        assertEquals("Hello {unknown}", result);
    }

    @Test
    void shouldHandleMixedContent() {
        String template = "No placeholders here";
        PrefixDto prefixDto = new PrefixDto(template);
        prefixDto.setDisplayName("name");
        prefixDto.setNumber("1");
        prefixDto.setAlphabet("A");
        assertEquals(template, prefixService.resolvePrefix(prefixDto));
    }
}
