package org.freshmarker.core.plugin;

import org.freshmarker.api.BuiltInProvider;
import org.freshmarker.api.FormatterProvider;
import org.freshmarker.api.TypeMapperProvider;
import org.freshmarker.core.TypeMapper;
import org.freshmarker.core.buildin.BuiltIn;
import org.freshmarker.core.buildin.BuiltInKey;
import org.freshmarker.core.buildin.BuiltInKeyBuilder;
import org.freshmarker.core.formatter.ClassicDateFormatter;
import org.freshmarker.core.formatter.ClassicDateTimeFormatter;
import org.freshmarker.core.formatter.ClassicTimeFormatter;
import org.freshmarker.core.formatter.Formatter;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.model.date.TemplateClassicDate;
import org.freshmarker.core.model.date.TemplateClassicDateTime;
import org.freshmarker.core.model.date.TemplateClassicTime;
import org.freshmarker.core.model.primitive.TemplateString;

import java.sql.Date;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public final class DatePluginProvider implements BuiltInProvider, TypeMapperProvider, FormatterProvider {

    @Override
    public Map<BuiltInKey, BuiltIn> provideBuiltIns() {
        BuiltInKeyBuilder<TemplateClassicDateTime> dateTimeBuilder = new BuiltInKeyBuilder<>(TemplateClassicDateTime.class);
        BuiltInKeyBuilder<TemplateClassicDate> dateBuilder = new BuiltInKeyBuilder<>(TemplateClassicDate.class);
        BuiltInKeyBuilder<TemplateClassicTime> timeBuilder = new BuiltInKeyBuilder<>(TemplateClassicTime.class);

        Map<BuiltInKey, BuiltIn> builtIns = new HashMap<>();
        builtIns.put(dateTimeBuilder.of("date"),
                (x, y, c) -> new TemplateClassicDate(new Date(((TemplateClassicDateTime) x).getValue().getTime())));
        builtIns.put(dateTimeBuilder.of("time"),
                (x, y, c) -> new TemplateClassicTime(new Time(((TemplateClassicDateTime) x).getValue().getTime())));
        builtIns.put(dateTimeBuilder.of("c"),
                (x, y, c) -> new TemplateString(new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss").format(((TemplateClassicDateTime) x).getValue())));
        builtIns.put(dateBuilder.of("date"), BuiltIn.identity());
        builtIns.put(dateBuilder.of("c"), (x, y, c) -> new TemplateString(String.valueOf(x)));
        builtIns.put(timeBuilder.of("time"), BuiltIn.identity());
        builtIns.put(timeBuilder.of("c"), (x, y, c) -> new TemplateString(String.valueOf(x)));
        return builtIns;
    }

    @Override
    public Map<Class<?>, TypeMapper> providerTypeMapper() {
        return Map.of(
            java.util.Date.class, o -> new TemplateClassicDateTime((java.util.Date) o),
            Date.class, o -> new TemplateClassicDate((Date) o),
            Time.class, o -> new TemplateClassicTime((Time) o));
    }

    @Override
    public Map<Class<? extends TemplateObject>, Formatter> providerFormatter() {
        return Map.of(
            TemplateClassicDateTime.class, new ClassicDateTimeFormatter("yyyy-MM-dd hh:mm:ss"),
            TemplateClassicDate.class, new ClassicDateFormatter("yyyy-MM-dd"),
            TemplateClassicTime.class, new ClassicTimeFormatter("hh:mm:ss"));
    }
}
