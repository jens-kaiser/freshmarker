package org.freshmarker.core.plugin;

import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.ProcessException;
import org.freshmarker.core.buildin.BuiltIn;
import org.freshmarker.core.buildin.BuiltInKey;
import org.freshmarker.core.buildin.BuiltInKeyBuilder;
import org.freshmarker.core.formatter.DateFormatter;
import org.freshmarker.core.formatter.DateTimeFormatter;
import org.freshmarker.core.formatter.DurationFormatter;
import org.freshmarker.core.formatter.Formatter;
import org.freshmarker.core.formatter.TimeFormatter;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.model.primitive.TemplateNumber;
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
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.Temporal;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.StringJoiner;
import java.util.function.Function;

public class TemporalPluginProvider implements PluginProvider {
    private static final BuiltInKeyBuilder<TemplateInstant> INSTANT_BUILDER = new BuiltInKeyBuilder<>(TemplateInstant.class);
    private static final BuiltInKeyBuilder<TemplateZonedDateTime> ZONED_DATE_TIME_BUILDER = new BuiltInKeyBuilder<>(TemplateZonedDateTime.class);
    private static final BuiltInKeyBuilder<TemplateLocalDateTime> DATE_TIME_BUILDER = new BuiltInKeyBuilder<>(TemplateLocalDateTime.class);
    private static final BuiltInKeyBuilder<TemplateLocalDate> DATE_BUILDER = new BuiltInKeyBuilder<>(TemplateLocalDate.class);
    private static final BuiltInKeyBuilder<TemplateLocalTime> TIME_BUILDER = new BuiltInKeyBuilder<>(TemplateLocalTime.class);
    private static final BuiltInKeyBuilder<TemplatePeriod> PERIOD_BUILDER = new BuiltInKeyBuilder<>(TemplatePeriod.class);

    private static final String AT_ZONE = "at_zone";
    private static final String STRING = "string";

    @Override
    public void registerBuildIn(Map<BuiltInKey, BuiltIn> builtIns) {
        builtIns.put(INSTANT_BUILDER.of("date_time"), (x, y, e) -> atZone((TemplateInstant) x, e.getZoneId()).toLocalDateTime());
        builtIns.put(INSTANT_BUILDER.of("date"), (x, y, e) -> atZone((TemplateInstant) x, e.getZoneId()).toLocalDate());
        builtIns.put(INSTANT_BUILDER.of("time"), (x, y, e) -> atZone((TemplateInstant) x, e.getZoneId()).toLocalTime());
        builtIns.put(INSTANT_BUILDER.of("c"), (x, y, e) -> new TemplateString(String.valueOf(x)));
        builtIns.put(INSTANT_BUILDER.of(STRING), (x, y, e) -> formatTemporal(y, e, ((TemplateInstant) x).getValue()));
        builtIns.put(INSTANT_BUILDER.of(AT_ZONE), (x, y, e) -> atZone((TemplateInstant) x, getZoneId(y, e)));

        builtIns.put(ZONED_DATE_TIME_BUILDER.of("date_time"), (x, y, e) -> ((TemplateZonedDateTime) x).toLocalDateTime());
        builtIns.put(ZONED_DATE_TIME_BUILDER.of("date"), (x, y, e) -> ((TemplateZonedDateTime) x).toLocalDate());
        builtIns.put(ZONED_DATE_TIME_BUILDER.of("time"), (x, y, e) -> ((TemplateZonedDateTime) x).toLocalTime());
        builtIns.put(ZONED_DATE_TIME_BUILDER.of("c"), (x, y, e) -> new TemplateString(String.valueOf(x)));
        builtIns.put(ZONED_DATE_TIME_BUILDER.of(STRING), (x, y, e) -> formatTemporal(y, e, ((TemplateZonedDateTime) x).getValue()));
        builtIns.put(ZONED_DATE_TIME_BUILDER.of(AT_ZONE), (x, y, e) -> atZone((TemplateZonedDateTime) x, getZoneId(y, e)));
        builtIns.put(ZONED_DATE_TIME_BUILDER.of("zone"),
                (x, y, e) -> new TemplateString(((TemplateZonedDateTime) x).getValue().getZone().toString()));

        builtIns.put(DATE_TIME_BUILDER.of("date"),
                (x, y, e) -> new TemplateLocalDate(((TemplateLocalDateTime) x).getValue().toLocalDate()));
        builtIns.put(DATE_TIME_BUILDER.of("time"),
                (x, y, e) -> new TemplateLocalTime(((TemplateLocalDateTime) x).getValue().toLocalTime()));
        builtIns.put(DATE_TIME_BUILDER.of("c"), (x, y, e) -> new TemplateString(String.valueOf(x)));
        builtIns.put(DATE_TIME_BUILDER.of(STRING), (x, y, e) -> formatTemporal(y, e, ((TemplateLocalDateTime) x).getValue()));
        builtIns.put(DATE_TIME_BUILDER.of(AT_ZONE), (x, y, e) -> atZone((TemplateLocalDateTime) x, getZoneId(y, e)));

        builtIns.put(DATE_BUILDER.of("date"), (x, y, e) -> x);
        builtIns.put(DATE_BUILDER.of("c"), (x, y, e) -> new TemplateString(String.valueOf(x)));
        builtIns.put(DATE_BUILDER.of(STRING), (x, y, e) -> formatTemporal(y, e, ((TemplateLocalDate) x).getValue()));
        builtIns.put(DATE_BUILDER.of("h"), (x, y, e) -> formatHuman(y, e, (TemplateLocalDate) x));
        builtIns.put(DATE_BUILDER.of("until"), (x, y, e) -> until((TemplateLocalDate) x, y, e));
        builtIns.put(DATE_BUILDER.of("since"), (x, y, e) -> since((TemplateLocalDate) x, y, e));

        builtIns.put(TIME_BUILDER.of("time"), (x, y, e) -> x);
        builtIns.put(TIME_BUILDER.of("c"), (x, y, e) -> new TemplateString(String.valueOf(x)));
        builtIns.put(TIME_BUILDER.of(STRING), (x, y, e) -> formatTemporal(y, e, ((TemplateLocalTime) x).getValue()));

        builtIns.put(PERIOD_BUILDER.of("h"), (x, y, e) -> getPeriod((TemplatePeriod) x, e));
        builtIns.put(PERIOD_BUILDER.of("c"), (x, y, e) -> new TemplateString(String.valueOf(x)));
        builtIns.put(PERIOD_BUILDER.of("years"), (x, y, e) -> new TemplateNumber(((TemplatePeriod) x).getValue().getYears()));
        builtIns.put(PERIOD_BUILDER.of("months"), (x, y, e) -> new TemplateNumber(((TemplatePeriod) x).getValue().getMonths()));
        builtIns.put(PERIOD_BUILDER.of("days"), (x, y, e) -> new TemplateNumber(((TemplatePeriod) x).getValue().getDays()));
    }

    private TemplateObject until(TemplateLocalDate x, List<TemplateObject> y, ProcessContext e) {
        LocalDate otherDate = y.isEmpty() ? LocalDate.now() : y.getFirst().evaluate(e, TemplateLocalDate.class).getValue();
        return new TemplatePeriod(x.getValue().until(otherDate));
    }

    private TemplateObject since(TemplateLocalDate x, List<TemplateObject> y, ProcessContext e) {
        LocalDate otherDate = y.isEmpty() ? LocalDate.now() : y.getFirst().evaluate(e, TemplateLocalDate.class).getValue();
        return new TemplatePeriod(otherDate.until(x.getValue()));
    }

    private TemplateObject formatHuman(List<TemplateObject> y, ProcessContext e, TemplateLocalDate value) {
        LocalDate now = y.isEmpty() ? LocalDate.now() : y.getFirst().evaluate(e, TemplateLocalDate.class).getValue();
        int days = now.until(value.getValue()).getDays();
        if (days < -2 || days > 2) {
            return value;
        }
        return new TemplateString(ResourceBundle.getBundle("freshmarker", e.getLocale()).getString("date." + days));
    }

    private static TemplateString formatTemporal(List<TemplateObject> y, ProcessContext e, Temporal value) {
        return new TemplateString(getDateTimeFormatter(y, e).withZone(e.getZoneId()).format(value));
    }

    private static TemplateString formatTemporal(List<TemplateObject> y, ProcessContext e, Instant value) {
        return new TemplateString(getDateTimeFormatter(y, e).withZone(ZoneOffset.UTC).format(value));
    }

    private static TemplateString formatTemporal(List<TemplateObject> y, ProcessContext e, ZonedDateTime value) {
        return new TemplateString(getDateTimeFormatter(y, e).format(value));
    }

    private static java.time.format.DateTimeFormatter getDateTimeFormatter(List<TemplateObject> y, ProcessContext e) {
        return java.time.format.DateTimeFormatter.ofPattern(getFormatString(y, e), e.getLocale());
    }

    private static String getFormatString(List<TemplateObject> y, ProcessContext e) {
        BuiltInHelper.checkParametersLength(y, 1);
        return y.getFirst().evaluate(e, TemplateString.class).getValue();
    }

    private static ZoneId getZoneId(List<TemplateObject> y, ProcessContext e) {
        BuiltInHelper.checkParametersLength(y, 1);
        String value = y.getFirst().evaluate(e, TemplateString.class).getValue();
        try {
            return ZoneId.of(value);
        } catch (Exception ex) {
            throw new ProcessException("no valid zoneId");
        }
    }

    private TemplateString getPeriod(TemplatePeriod period, ProcessContext e) {
        if (period.getValue().isZero()) {
            return TemplateString.EMPTY;
        }
        ResourceBundle resourceBundle = ResourceBundle.getBundle("freshmarker", e.getLocale());
        StringJoiner stringJoiner = new StringJoiner(", ");
        get(stringJoiner, period.getValue().getYears(), resourceBundle, "year");
        get(stringJoiner, period.getValue().getMonths(), resourceBundle, "month");
        get(stringJoiner, period.getValue().getDays(), resourceBundle, "day");
        return new TemplateString(stringJoiner.toString());
    }

    private void get(StringJoiner stringJoiner, int value, ResourceBundle resourceBundle, String key) {
        switch (value) {
            case 0 -> {
            }
            case 1, -1 -> stringJoiner.add(value + " " + resourceBundle.getString("period." + key));
            default -> stringJoiner.add(value + " " + resourceBundle.getString("period." + key + "s"));
        }
    }

    private TemplateZonedDateTime atZone(TemplateLocalDateTime localDateTime, ZoneId zoneId) {
        return new TemplateZonedDateTime(localDateTime.getValue().atZone(zoneId));
    }

    private TemplateZonedDateTime atZone(TemplateZonedDateTime zonedDateTime, ZoneId zoneId) {
        return new TemplateZonedDateTime(zonedDateTime.getValue().withZoneSameInstant(zoneId));
    }

    private TemplateZonedDateTime atZone(TemplateInstant instant, ZoneId zoneId) {
        return new TemplateZonedDateTime(instant.getValue().atZone(zoneId));
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
        formatter.put(TemplateInstant.class, new DateTimeFormatter("uuuu-MM-dd hh:mm:ss VV", ZoneOffset.UTC));
        formatter.put(TemplateZonedDateTime.class, new DateTimeFormatter("yyyy-MM-dd hh:mm:ss VV"));
        formatter.put(TemplateLocalDateTime.class, new DateTimeFormatter("yyyy-MM-dd hh:mm:ss"));
        formatter.put(TemplateLocalDate.class, new DateFormatter("yyyy-MM-dd"));
        formatter.put(TemplateLocalTime.class, new TimeFormatter("hh:mm:ss"));
        formatter.put(TemplateDuration.class, new DurationFormatter());
        formatter.put(TemplatePeriod.class, new DurationFormatter());
    }
}
