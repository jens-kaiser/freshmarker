package org.freshmarker.core.plugin;

import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.buildin.BuiltIn;
import org.freshmarker.core.buildin.BuiltInKey;
import org.freshmarker.core.buildin.BuiltInKeyBuilder;
import org.freshmarker.core.buildin.FunctionalBuiltIn;
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
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class DatePluginProvider implements PluginProvider {
    private static final BuiltInKeyBuilder<TemplateClassicDateTime> DATE_TIME_BUILDER = new BuiltInKeyBuilder<>(TemplateClassicDateTime.class);

    private static final BuiltInKeyBuilder<TemplateClassicDate> DATE_BUILDER = new BuiltInKeyBuilder<>(TemplateClassicDate.class);

    private static final BuiltInKeyBuilder<TemplateClassicTime> TIME_BUILDER = new BuiltInKeyBuilder<>(TemplateClassicTime.class);

    @Override
    public void registerBuildIn(Map<BuiltInKey, BuiltIn> builtIns) {
        builtIns.put(DATE_TIME_BUILDER.of("date"), new FunctionalBuiltIn(
                (x, y, c) -> new TemplateClassicDate(new Date(((TemplateClassicDateTime) x).getValue().getTime()))));
        builtIns.put(DATE_TIME_BUILDER.of("time"), new FunctionalBuiltIn(
                (x, y, c) -> new TemplateClassicTime(new Time(((TemplateClassicDateTime) x).getValue().getTime()))));
        builtIns.put(DATE_TIME_BUILDER.of("c"), new FunctionalBuiltIn(
                (x, y, c) -> new TemplateString(
                        new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss").format(((TemplateClassicDateTime) x).getValue()))));
        builtIns.put(DATE_BUILDER.of("date"), new FunctionalBuiltIn((x, y, c) -> x));
        builtIns.put(DATE_BUILDER.of("c"), new FunctionalBuiltIn((x, y, c) -> new TemplateString(String.valueOf(x))));
        builtIns.put(TIME_BUILDER.of("time"), new FunctionalBuiltIn((x, y, c) -> x));
        builtIns.put(TIME_BUILDER.of("c"), new FunctionalBuiltIn((x, y, c) -> new TemplateString(String.valueOf(x))));
    }

    @Override
    public void registerMapper(Map<Class<?>, Function<Object, TemplateObject>> mapper) {
        mapper.put(java.util.Date.class, o -> new TemplateClassicDateTime((java.util.Date) o));
        mapper.put(Date.class, o -> new TemplateClassicDate((Date) o));
        mapper.put(Time.class, o -> new TemplateClassicTime((Time) o));
    }

    @Override
    public void registerFormatter(Map<Class<? extends TemplateObject>, Formatter> formatter) {
        formatter.put(TemplateClassicDateTime.class, new ClassicDateTimeFormatter("yyyy-MM-dd hh:mm:ss"));
        formatter.put(TemplateClassicDate.class, new ClassicDateFormatter("yyyy-MM-dd"));
        formatter.put(TemplateClassicTime.class, new ClassicTimeFormatter("hh:mm:ss"));
    }
}
