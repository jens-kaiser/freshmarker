package org.freshmarker.core.extension;

import org.freshmarker.api.Formatter;
import org.freshmarker.api.extension.FormatterProvider;
import org.freshmarker.core.formatter.DateFormatter;
import org.freshmarker.core.formatter.DateTimeFormatter;
import org.freshmarker.core.formatter.NumberFormatter;
import org.freshmarker.core.formatter.TimeFormatter;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.model.primitive.TemplateNumber;
import org.freshmarker.core.model.temporal.TemplateInstant;
import org.freshmarker.core.model.temporal.TemplateLocalDate;
import org.freshmarker.core.model.temporal.TemplateLocalDateTime;
import org.freshmarker.core.model.temporal.TemplateLocalTime;
import org.freshmarker.core.model.temporal.TemplateOffsetDateTime;
import org.freshmarker.core.model.temporal.TemplateZonedDateTime;

import java.time.ZoneId;
import java.util.Map;

public class PatternBasedFormatterProvider implements FormatterProvider {
    private final String type;
    private final String pattern;

    public PatternBasedFormatterProvider(String type, String pattern) {
        this.type = type;
        this.pattern = pattern;
    }

    @Override
    public Map<Class<? extends TemplateObject>, Formatter> providerFormatter() {
        return switch (type) {
            case "number" -> Map.of(TemplateNumber.class, new NumberFormatter(pattern));
            case "zoned-date-time" -> Map.of(
                    TemplateZonedDateTime.class, new DateTimeFormatter(pattern),
                    TemplateInstant.class, new DateTimeFormatter(pattern, ZoneId.systemDefault()));
            case "offset-date-time" -> Map.of(TemplateOffsetDateTime.class, new DateTimeFormatter(pattern));
            case "date-time" -> Map.of(TemplateLocalDateTime.class, new DateTimeFormatter(pattern));
            case "date" -> Map.of(TemplateLocalDate.class, new DateFormatter(pattern));
            case "time" -> Map.of(TemplateLocalTime.class, new TimeFormatter(pattern, ZoneId.systemDefault()));
            default -> throw new IllegalStateException("Unexpected value: " + type);
        };
    }
}
