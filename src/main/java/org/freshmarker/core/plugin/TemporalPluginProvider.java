package org.freshmarker.core.plugin;

import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.ProcessException;
import org.freshmarker.core.buildin.BuiltIn;
import org.freshmarker.core.buildin.BuiltInKey;
import org.freshmarker.core.buildin.BuiltInKeyBuilder;
import org.freshmarker.core.buildin.FunctionalBuiltIn;
import org.freshmarker.core.formatter.DateFormatter;
import org.freshmarker.core.formatter.DateTimeFormatter;
import org.freshmarker.core.formatter.DurationFormatter;
import org.freshmarker.core.formatter.Formatter;
import org.freshmarker.core.formatter.TimeFormatter;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.model.primitive.TemplateString;
import org.freshmarker.core.model.temporal.TemplateDuration;
import org.freshmarker.core.model.temporal.TemplateInstant;
import org.freshmarker.core.model.temporal.TemplateLocalDate;
import org.freshmarker.core.model.temporal.TemplateLocalDateTime;
import org.freshmarker.core.model.temporal.TemplateLocalTime;
import org.freshmarker.core.model.temporal.TemplatePeriod;
import org.freshmarker.core.model.temporal.TemplateZonedDateTime;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.Temporal;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class TemporalPluginProvider implements PluginProvider {
    private static final BuiltInKeyBuilder<TemplateInstant> INSTANT_BUILDER = new BuiltInKeyBuilder<>(TemplateInstant.class);
    private static final BuiltInKeyBuilder<TemplateZonedDateTime> ZONED_DATE_TIME_BUILDER = new BuiltInKeyBuilder<>(TemplateZonedDateTime.class);
    private static final BuiltInKeyBuilder<TemplateLocalDateTime> DATE_TIME_BUILDER = new BuiltInKeyBuilder<>(TemplateLocalDateTime.class);
    private static final BuiltInKeyBuilder<TemplateLocalDate> DATE_BUILDER = new BuiltInKeyBuilder<>(TemplateLocalDate.class);
    private static final BuiltInKeyBuilder<TemplateLocalTime> TIME_BUILDER = new BuiltInKeyBuilder<>(TemplateLocalTime.class);

    @Override
    public void registerBuildIn(Map<BuiltInKey, BuiltIn> builtIns) {
        builtIns.put(INSTANT_BUILDER.of("date"),
                new FunctionalBuiltIn((TemplateObject x, List<TemplateObject> y, ProcessContext e) -> new TemplateLocalDate(
                        ((TemplateInstant) x).getValue().atZone(ZoneId.systemDefault()).toLocalDate())));
        builtIns.put(INSTANT_BUILDER.of("time"),
                new FunctionalBuiltIn((TemplateObject x, List<TemplateObject> y, ProcessContext e) -> new TemplateLocalTime(
                        ((TemplateInstant) x).getValue().atZone(ZoneId.systemDefault()).toLocalTime())));
        builtIns.put(INSTANT_BUILDER.of("c"), new FunctionalBuiltIn(
                (TemplateObject x, List<TemplateObject> y, ProcessContext e) -> new TemplateString(String.valueOf(x))));
        builtIns.put(INSTANT_BUILDER.of("string"), new FunctionalBuiltIn(
                (TemplateObject x, List<TemplateObject> y, ProcessContext e) -> formatTemporal(y, e, ((TemplateInstant) x).getValue())));
        builtIns.put(INSTANT_BUILDER.of("at_zone"), new FunctionalBuiltIn(
                (TemplateObject x, List<TemplateObject> y, ProcessContext e) -> ((TemplateInstant) x).atZone(getZoneIdName(y, e))));

        builtIns.put(ZONED_DATE_TIME_BUILDER.of("date"),
                new FunctionalBuiltIn((TemplateObject x, List<TemplateObject> y, ProcessContext e) -> new TemplateLocalDate(
                        ((TemplateZonedDateTime) x).getValue().toLocalDate())));
        builtIns.put(ZONED_DATE_TIME_BUILDER.of("time"),
                new FunctionalBuiltIn((TemplateObject x, List<TemplateObject> y, ProcessContext e) -> new TemplateLocalTime(
                        ((TemplateZonedDateTime) x).getValue().toLocalTime())));
        builtIns.put(ZONED_DATE_TIME_BUILDER.of("c"), new FunctionalBuiltIn(
                (TemplateObject x, List<TemplateObject> y, ProcessContext e) -> new TemplateString(String.valueOf(x))));
        builtIns.put(ZONED_DATE_TIME_BUILDER.of("string"), new FunctionalBuiltIn(
                (TemplateObject x, List<TemplateObject> y, ProcessContext e) -> formatTemporal(y, e, ((TemplateZonedDateTime) x).getValue())));
        builtIns.put(ZONED_DATE_TIME_BUILDER.of("at_zone"), new FunctionalBuiltIn(
                (TemplateObject x, List<TemplateObject> y, ProcessContext e) -> ((TemplateZonedDateTime) x).atZone(getZoneIdName(y, e))));

        builtIns.put(DATE_TIME_BUILDER.of("date"),
                new FunctionalBuiltIn((TemplateObject x, List<TemplateObject> y, ProcessContext e) -> new TemplateLocalDate(
                        ((TemplateLocalDateTime) x).getValue().toLocalDate())));
        builtIns.put(DATE_TIME_BUILDER.of("time"),
                new FunctionalBuiltIn((TemplateObject x, List<TemplateObject> y, ProcessContext e) -> new TemplateLocalTime(
                        ((TemplateLocalDateTime) x).getValue().toLocalTime())));
        builtIns.put(DATE_TIME_BUILDER.of("c"), new FunctionalBuiltIn(
                (TemplateObject x, List<TemplateObject> y, ProcessContext e) -> new TemplateString(String.valueOf(x))));
        builtIns.put(DATE_TIME_BUILDER.of("string"), new FunctionalBuiltIn(
                (TemplateObject x, List<TemplateObject> y, ProcessContext e) -> formatTemporal(y, e, ((TemplateLocalDateTime) x).getValue())));
        builtIns.put(DATE_TIME_BUILDER.of("at_zone"), new FunctionalBuiltIn(
                (TemplateObject x, List<TemplateObject> y, ProcessContext e) -> ((TemplateLocalDateTime) x).atZone(getZoneIdName(y, e))));

        builtIns.put(DATE_BUILDER.of("date"),
                new FunctionalBuiltIn((TemplateObject x, List<TemplateObject> y, ProcessContext e) -> x));
        builtIns.put(DATE_BUILDER.of("c"), new FunctionalBuiltIn(
                (TemplateObject x, List<TemplateObject> y, ProcessContext e) -> new TemplateString(String.valueOf(x))));
        builtIns.put(DATE_BUILDER.of("string"), new FunctionalBuiltIn(
                (TemplateObject x, List<TemplateObject> y, ProcessContext e) -> formatTemporal(y, e, ((TemplateLocalDate) x).getValue())));

        builtIns.put(TIME_BUILDER.of("time"),
                new FunctionalBuiltIn((TemplateObject x, List<TemplateObject> y, ProcessContext e) -> x));
        builtIns.put(TIME_BUILDER.of("c"), new FunctionalBuiltIn(
                (TemplateObject x, List<TemplateObject> y, ProcessContext e) -> new TemplateString(String.valueOf(x))));
        builtIns.put(TIME_BUILDER.of("string"), new FunctionalBuiltIn(
                (TemplateObject x, List<TemplateObject> y, ProcessContext e) -> formatTemporal(y, e, ((TemplateLocalTime) x).getValue())));
    }

    private static TemplateString formatTemporal(List<TemplateObject> y, ProcessContext e, Temporal value) {
        String pattern = getFormatString(y, e);
        return new TemplateString(java.time.format.DateTimeFormatter.ofPattern(pattern, e.getEnvironment().getLocale()).withZone(ZoneId.systemDefault()).format(value));
    }

    private static TemplateString formatTemporal(List<TemplateObject> y, ProcessContext e, Instant value) {
        String pattern = getFormatString(y, e);
        return new TemplateString(java.time.format.DateTimeFormatter.ofPattern(pattern, e.getEnvironment().getLocale()).withZone(ZoneId.of("UTC")).format(value));
    }

    private static TemplateString formatTemporal(List<TemplateObject> y, ProcessContext e, ZonedDateTime value) {
        String pattern = getFormatString(y, e);
        return new TemplateString(java.time.format.DateTimeFormatter.ofPattern(pattern, e.getEnvironment().getLocale()).format(value));
    }

    private static String getFormatString(List<TemplateObject> y, ProcessContext e) {
        if (y.isEmpty()) {
            throw new ProcessException("missing format parameter");
        }
        return y.get(0).evaluateToObject(e).asString().map(TemplateString::getValue).orElseThrow(() -> new ProcessException("invalid format parameter"));
    }

    private static ZoneId getZoneIdName(List<TemplateObject> y, ProcessContext e) {
        if (y.size() != 1) {
            throw new IllegalArgumentException("wrong parameter count");
        }
        return ZoneId.of(y.get(0).evaluateToObject(e).asString().map(TemplateString::getValue).orElseThrow(() -> new IllegalArgumentException("no valid zoneId")));
    }

    @Override
    public void registerMapper(Map<Class<?>, Function<Object, TemplateObject>> mapper) {
        mapper.put(Instant.class, o -> new TemplateInstant((Instant) o));
        mapper.put(ZonedDateTime.class, o -> new TemplateZonedDateTime((ZonedDateTime) o));
        mapper.put(LocalDateTime.class, o -> new TemplateLocalDateTime((LocalDateTime) o));
        mapper.put(LocalDate.class, o -> new TemplateLocalDate((LocalDate) o));
        mapper.put(LocalTime.class, o -> new TemplateLocalTime((LocalTime) o));
        mapper.put(Duration.class, o -> new TemplateDuration((Duration) o));
        mapper.put(Period.class, o -> new TemplatePeriod((Period) o));
    }

    @Override
    public void registerFormatter(Map<Class<? extends TemplateObject>, Formatter> formatter) {
        formatter.put(TemplateInstant.class, new DateTimeFormatter("uuuu-MM-dd hh:mm:ss VV", ZoneId.of("UTC")));
        formatter.put(TemplateZonedDateTime.class, new DateTimeFormatter("yyyy-MM-dd hh:mm:ss VV"));
        formatter.put(TemplateLocalDateTime.class, new DateTimeFormatter("yyyy-MM-dd hh:mm:ss"));
        formatter.put(TemplateLocalDate.class, new DateFormatter("yyyy-MM-dd"));
        formatter.put(TemplateLocalTime.class, new TimeFormatter("hh:mm:ss"));
        formatter.put(TemplateDuration.class, new DurationFormatter());
        formatter.put(TemplatePeriod.class, new DurationFormatter());
    }
}
