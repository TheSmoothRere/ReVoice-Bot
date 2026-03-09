package io.github.thesmoothrere.revoicebot.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class PrefixService {
    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\{(.*?)}");

    public String resolvePrefix(String template, String displayName, String number, String alphabet) {
        if (template == null || template.isBlank()) {
            return "";
        }

        Map<String, String> context = Map.of(
                "user.name", displayName,
                "number", number,
                "alphabet", alphabet
        );

        StringBuilder result = new StringBuilder();
        Matcher matcher = PLACEHOLDER_PATTERN.matcher(template);

        while (matcher.find()) {
            String key = matcher.group(1);
            String replacement = context.getOrDefault(key, "{" + key + "}");
            matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(result);

        log.debug("Resolved prefix template: {}", result.toString());
        return result.toString();
    }
}
