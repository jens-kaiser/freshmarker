package org.freshmarker.core.fragment;

import ftl.ast.SettingInstruction;
import org.freshmarker.api.Formatter;
import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.ProcessException;
import org.freshmarker.core.formatter.ClassicDateFormatter;
import org.freshmarker.core.formatter.ClassicDateTimeFormatter;
import org.freshmarker.core.formatter.ClassicTimeFormatter;
import org.freshmarker.core.formatter.DateFormatter;
import org.freshmarker.core.formatter.DateTimeFormatter;
import org.freshmarker.core.formatter.TimeFormatter;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.model.date.TemplateClassicDate;
import org.freshmarker.core.model.date.TemplateClassicDateTime;
import org.freshmarker.core.model.date.TemplateClassicTime;
import org.freshmarker.core.model.primitive.TemplateString;
import org.freshmarker.core.model.temporal.TemplateInstant;
import org.freshmarker.core.model.temporal.TemplateLocalDate;
import org.freshmarker.core.model.temporal.TemplateLocalDateTime;
import org.freshmarker.core.model.temporal.TemplateLocalTime;
import org.freshmarker.core.model.temporal.TemplateZonedDateTime;

import java.time.ZoneId;
import java.util.Locale;
import java.util.Map;

public record SettingFragment(String name, TemplateObject expression, SettingInstruction ftl) implements Fragment {

    @Override
    public void process(ProcessContext context) {
        TemplateObject setting = expression.evaluateToObject(context);
        switch (name) {
            case "locale" -> context.push(Locale.forLanguageTag(setting.evaluate(context, TemplateString.class).getValue()));
            case "date_format" -> processDateFormat(context, setting);
            case "time_format" -> processTimeFormat(context, setting);
            case "datetime_format" -> processDateTimeFormat(context, setting);
            case "zoned_datetime_format" -> processZonedDateTimeFormat(context, setting);
            case "zone_id" -> context.push(ZoneId.of(setting.evaluate(context, TemplateString.class).getValue()));
            default -> throw new ProcessException("unknown setting: " + name, ftl);
        }
    }

    private static void processZonedDateTimeFormat(ProcessContext context, TemplateObject setting) {
        String value = setting.evaluate(context, TemplateString.class).getValue();
        Map<Class<? extends TemplateObject>, Formatter> formatter = Map.of(
                TemplateInstant.class, new DateTimeFormatter(value, context.getZoneId()), TemplateZonedDateTime.class, new DateTimeFormatter(value));
        context.pushFormatter(formatter);
    }

    private static void processDateTimeFormat(ProcessContext context, TemplateObject setting) {
        String value = setting.evaluate(context, TemplateString.class).getValue();
        Map<Class<? extends TemplateObject>, Formatter> formatter = Map.of(
                TemplateLocalDateTime.class, new DateTimeFormatter(value), TemplateClassicDateTime.class, new ClassicDateTimeFormatter(value));
        context.pushFormatter(formatter);
    }

    private static void processTimeFormat(ProcessContext context, TemplateObject setting) {
        String value = setting.evaluate(context, TemplateString.class).getValue();
        Map<Class<? extends TemplateObject>, Formatter> formatter = Map.of(
                TemplateLocalTime.class, new TimeFormatter(value, context.getZoneId()), TemplateClassicTime.class, new ClassicTimeFormatter(value));
        context.pushFormatter(formatter);
    }

    private static void processDateFormat(ProcessContext context, TemplateObject setting) {
        String value = setting.evaluate(context, TemplateString.class).getValue();
        Map<Class<? extends TemplateObject>, Formatter> formatter = Map.of(
                TemplateLocalDate.class, new DateFormatter(value), TemplateClassicDate.class, new ClassicDateFormatter(value));
        context.pushFormatter(formatter);
    }

    @Override
    public <R> R accept(TemplateVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
