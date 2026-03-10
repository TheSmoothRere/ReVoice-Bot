package io.github.thesmoothrere.revoicebot.service;

import io.github.thesmoothrere.revoicebot.dto.PrefixDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class PrefixService {
    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\{(.*?)}");

    public String resolvePrefix(PrefixDto prefixDto) {
        if (prefixDto.getTemplate() == null || prefixDto.getTemplate().isBlank()) return "Generated Voice";

        Map<String, String> context = Map.of(
                "user.name", prefixDto.getDisplayName(),
                "number", prefixDto.getNumber(),
                "alphabet", prefixDto.getAlphabet()
        );

        StringBuilder result = new StringBuilder();
        Matcher matcher = PLACEHOLDER_PATTERN.matcher(prefixDto.getTemplate());

        while (matcher.find()) {
            String key = matcher.group(1);
            String replacement = context.getOrDefault(key, "{" + key + "}");
            matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(result);

        log.debug("Resolved prefix template: {}", result);
        return result.toString();
    }
}
