package org.freshmarker.core.formatter;

import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.model.primitive.TemplateNumber;
import org.freshmarker.core.model.temporal.TemplateInstant;
import org.freshmarker.core.model.temporal.TemplateLocalDate;
import org.freshmarker.core.model.temporal.TemplateLocalDateTime;
import org.freshmarker.core.model.temporal.TemplateLocalTime;
import org.freshmarker.core.model.temporal.TemplateZonedDateTime;

import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;

public class FormatterRegistry {
    private final Map<Class<? extends TemplateObject>, Formatter> formatter;

    public FormatterRegistry(Map<Class<? extends TemplateObject>, Formatter> formatter) {
        this.formatter = formatter;
    }

    public void registerFormatter(Class<? extends TemplateObject> type, Formatter formatter) {
        this.formatter.put(type, formatter);
    }

    public void registerNumberFormatter(String pattern) {
        this.formatter.put(TemplateNumber.class, new NumberFormatter(pattern));
    }

    public void registerFormatter(String type, String pattern) {
        switch (type) {
            case "number" -> registerNumberFormatter(pattern);
            case "zoned-date-time" -> {
                formatter.put(TemplateZonedDateTime.class, new DateTimeFormatter(pattern));
                formatter.put(TemplateInstant.class, new DateTimeFormatter(pattern, ZoneOffset.UTC));
            }
            case "date-time" -> formatter.put(TemplateLocalDateTime.class, new DateTimeFormatter(pattern));
            case "date" -> formatter.put(TemplateLocalDate.class, new DateFormatter(pattern));
            case "time" -> formatter.put(TemplateLocalTime.class, new TimeFormatter(pattern));
            default -> throw new IllegalStateException("Unexpected value: " + type);
        };
    }

    public Map<Class<? extends TemplateObject>, Formatter> getFormatter() {
        return new HashMap<>(formatter);
    }
}
