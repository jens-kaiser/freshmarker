package org.freshmarker.core.plugin;

import org.freshmarker.api.extension.BuiltInProvider;
import org.freshmarker.api.Formatter;
import org.freshmarker.api.extension.FormatterProvider;
import org.freshmarker.api.extension.Register;
import org.freshmarker.api.extension.TypeMapperProvider;
import org.freshmarker.api.extension.support.BuiltInRegister;
import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.ProcessException;
import org.freshmarker.api.TypeMapper;
import org.freshmarker.api.BuiltIn;
import org.freshmarker.core.formatter.DateFormatter;
import org.freshmarker.core.formatter.DateTimeFormatter;
import org.freshmarker.core.formatter.DurationFormatter;
import org.freshmarker.core.formatter.TimeFormatter;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.model.primitive.TemplateBoolean;
import org.freshmarker.core.model.primitive.TemplateEnum;
import org.freshmarker.core.model.primitive.TemplateNumber;
import org.freshmarker.core.model.primitive.TemplateString;
import org.freshmarker.core.model.temporal.TemplateDuration;
import org.freshmarker.core.model.temporal.TemplateInstant;
import org.freshmarker.core.model.temporal.TemplateLocalDate;
import org.freshmarker.core.model.temporal.TemplateLocalDateTime;
import org.freshmarker.core.model.temporal.TemplateLocalTime;
import org.freshmarker.core.model.temporal.TemplateMonthDay;
import org.freshmarker.core.model.temporal.TemplatePeriod;
import org.freshmarker.core.model.temporal.TemplateYear;
import org.freshmarker.core.model.temporal.TemplateYearMonth;
import org.freshmarker.core.model.temporal.TemplateZonedDateTime;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.MonthDay;
import java.time.Period;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.Temporal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.StringJoiner;

public final class TemporalPluginProvider implements BuiltInProvider, TypeMapperProvider, FormatterProvider {
    private static final String AT_ZONE = "at_zone";
    private static final String STRING = "string";
    private static final String YEAR = "year";
    private static final String MONTH = "month";
    private static final String C = "c";
    private static final String DAY = "day";

    private TemplateZonedDateTime to(ZonedDateTime dateTime) {
        return new TemplateZonedDateTime(dateTime);
    }

    private TemplateLocalDateTime to(LocalDateTime dateTime) {
        return new TemplateLocalDateTime(dateTime);
    }

    private TemplateLocalDate to(LocalDate date) {
        return new TemplateLocalDate(date);
    }

    private TemplateLocalTime to(LocalTime time) {
        return new TemplateLocalTime(time);
    }

    private TemplateString toString(Object value) {
        return new TemplateString(value.toString());
    }

    private LocalDate now(ProcessContext e) {
       return LocalDate.now(e.getBaseEnvironment().getClock().withZone(e.getZoneId()));
    }

    private TemplateObject until(TemplateLocalDate x, List<TemplateObject> y, ProcessContext e) {
        LocalDate otherDate = y.isEmpty() ? now(e) : y.getFirst().evaluate(e, TemplateLocalDate.class).getValue();
        return new TemplatePeriod(x.getValue().until(otherDate));
    }

    private TemplateObject since(TemplateLocalDate x, List<TemplateObject> y, ProcessContext e) {
        LocalDate otherDate = y.isEmpty() ? now(e) : y.getFirst().evaluate(e, TemplateLocalDate.class).getValue();
        return new TemplatePeriod(otherDate.until(x.getValue()));
    }

    private TemplateObject formatHuman(List<TemplateObject> y, ProcessContext e, TemplateLocalDate value) {
        LocalDate now = y.isEmpty() ? now(e) : y.getFirst().evaluate(e, TemplateLocalDate.class).getValue();
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
        return new DateTimeFormatter(getFormatString(y, e)).getFormatter(e.getLocale());
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
        } catch (RuntimeException ex) {
            throw new ProcessException("no valid zoneId", ex);
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
                // intentionally left blank
            }
            case 1, -1 -> stringJoiner.add(value + " " + resourceBundle.getString("period." + key));
            default -> stringJoiner.add(value + " " + resourceBundle.getString("period." + key + "s"));
        }
    }

    @Override
    public Register<Class<? extends TemplateObject>, String, BuiltIn> provideBuiltInRegister() {
        BuiltInRegister register = new BuiltInRegister();
        register.add(TemplateInstant.class, "date_time", (x, y, e) -> to(((TemplateInstant) x).getValue().atZone(e.getZoneId()).toLocalDateTime()));
        register.add(TemplateInstant.class, "date", (x, y, e) -> to(((TemplateInstant) x).getValue().atZone(e.getZoneId()).toLocalDate()));
        register.add(TemplateInstant.class, "time", (x, y, e) -> to(((TemplateInstant) x).getValue().atZone(e.getZoneId()).toLocalTime()));
        register.add(TemplateInstant.class, C, BuiltIn.string());
        register.add(TemplateInstant.class, STRING, (x, y, e) -> formatTemporal(y, e, ((TemplateInstant) x).getValue()));
        register.add(TemplateInstant.class, AT_ZONE, (x, y, e) -> to(((TemplateInstant) x).getValue().atZone(getZoneId(y, e))));

        register.add(TemplateZonedDateTime.class, "date_time", (x, y, e) -> to(((TemplateZonedDateTime) x).getValue().toLocalDateTime()));
        register.add(TemplateZonedDateTime.class, "date", (x, y, e) -> to(((TemplateZonedDateTime) x).getValue().toLocalDate()));
        register.add(TemplateZonedDateTime.class, "time", (x, y, e) -> to(((TemplateZonedDateTime) x).getValue().toLocalTime()));
        register.add(TemplateZonedDateTime.class, C, BuiltIn.string());
        register.add(TemplateZonedDateTime.class, STRING, (x, y, e) -> formatTemporal(y, e, ((TemplateZonedDateTime) x).getValue()));
        register.add(TemplateZonedDateTime.class, AT_ZONE, (x, y, e) -> to(((TemplateZonedDateTime) x).getValue().withZoneSameInstant(getZoneId(y, e))));
        register.add(TemplateZonedDateTime.class, "zone", (x, y, e) -> toString(((TemplateZonedDateTime) x).getValue().getZone()));
        register.add(TemplateZonedDateTime.class, DAY, (x, y, e) -> TemplateNumber.of(((TemplateZonedDateTime) x).getValue().getDayOfMonth()));

        register.add(TemplateLocalDateTime.class, "date", (x, y, e) -> to(((TemplateLocalDateTime) x).getValue().toLocalDate()));
        register.add(TemplateLocalDateTime.class, "time", (x, y, e) -> to(((TemplateLocalDateTime) x).getValue().toLocalTime()));
        register.add(TemplateLocalDateTime.class, C, BuiltIn.string());
        register.add(TemplateLocalDateTime.class, STRING, (x, y, e) -> formatTemporal(y, e, ((TemplateLocalDateTime) x).getValue()));
        register.add(TemplateLocalDateTime.class, AT_ZONE, (x, y, e) -> to(((TemplateLocalDateTime) x).getValue().atZone(getZoneId(y, e))));
        register.add(TemplateLocalDateTime.class, YEAR, (x, y, e) -> new TemplateYear(Year.of(((TemplateLocalDateTime) x).getValue().getYear())));
        register.add(TemplateLocalDateTime.class, MONTH, (x, y, e) -> new TemplateEnum<>(((TemplateLocalDateTime) x).getValue().getMonth()));
        register.add(TemplateLocalDateTime.class, DAY, (x, y, e) -> TemplateNumber.of(((TemplateLocalDateTime) x).getValue().getDayOfMonth()));

        register.add(TemplateLocalDate.class, "date", BuiltIn.identity());
        register.add(TemplateLocalDate.class, C, BuiltIn.string());
        register.add(TemplateLocalDate.class, STRING, (x, y, e) -> formatTemporal(y, e, ((TemplateLocalDate) x).getValue()));
        register.add(TemplateLocalDate.class, "h", (x, y, e) -> formatHuman(y, e, (TemplateLocalDate) x));
        register.add(TemplateLocalDate.class, "until", (x, y, e) -> until((TemplateLocalDate) x, y, e));
        register.add(TemplateLocalDate.class, "since", (x, y, e) -> since((TemplateLocalDate) x, y, e));
        register.add(TemplateLocalDate.class, YEAR, (x, y, e) -> new TemplateYear(Year.of(((TemplateLocalDateTime) x).getValue().getYear())));
        register.add(TemplateLocalDate.class, MONTH, (x, y, e) -> new TemplateEnum<>(((TemplateLocalDateTime) x).getValue().getMonth()));

        register.add(TemplateLocalTime.class, "time", BuiltIn.identity());
        register.add(TemplateLocalTime.class, C, BuiltIn.string());
        register.add(TemplateLocalTime.class, STRING, (x, y, e) -> formatTemporal(y, e, ((TemplateLocalTime) x).getValue()));

        register.add(TemplatePeriod.class, "h", (x, y, e) -> getPeriod((TemplatePeriod) x, e));
        register.add(TemplatePeriod.class, C, BuiltIn.string());
        register.add(TemplatePeriod.class, "years", (x, y, e) -> new TemplateNumber(((TemplatePeriod) x).getValue().getYears()));
        register.add(TemplatePeriod.class, "months", (x, y, e) -> new TemplateNumber(((TemplatePeriod) x).getValue().getMonths()));
        register.add(TemplatePeriod.class, "days", (x, y, e) -> new TemplateNumber(((TemplatePeriod) x).getValue().getDays()));

        register.add(TemplateYear.class, C, BuiltIn.string());
        register.add(TemplateYear.class, "is_leap", (x, y, e) -> TemplateBoolean.from(((TemplateYear) x).getValue().isLeap()));
        register.add(TemplateYear.class, YEAR, BuiltIn.identity());

        register.add(TemplateYearMonth.class, C, BuiltIn.string());
        register.add(TemplateYearMonth.class, "is_leap", (x, y, e) -> TemplateBoolean.from(new TemplateYear(((TemplateYearMonth) x).getValue().getYear()).isLeap()));
        register.add(TemplateYearMonth.class, YEAR, (x, y, e) -> new TemplateYear(((TemplateYearMonth) x).getValue().getYear()));
        register.add(TemplateYearMonth.class, MONTH, (x, y, e) -> new TemplateEnum<>(((TemplateYearMonth) x).getValue().getMonth()));

        register.add(TemplateMonthDay.class, C, BuiltIn.string());
        register.add(TemplateMonthDay.class, MONTH, (x, y, e) -> new TemplateEnum<>(((TemplateMonthDay) x).getValue().getMonth()));
        register.add(TemplateMonthDay.class, DAY, (x, y, e) -> TemplateNumber.of(((TemplateMonthDay) x).getValue().getDayOfMonth()));
        return register;
    }

    @Override
    public Map<Class<? extends TemplateObject>, Formatter> providerFormatter() {
        Map<Class<? extends TemplateObject>, Formatter> formatter = new HashMap<>();
        formatter.put(TemplateInstant.class, new DateTimeFormatter("uuuu-MM-dd hh:mm:ss VV", ZoneOffset.UTC));
        formatter.put(TemplateZonedDateTime.class, new DateTimeFormatter("yyyy-MM-dd hh:mm:ss VV"));
        formatter.put(TemplateLocalDateTime.class, new DateTimeFormatter("yyyy-MM-dd hh:mm:ss"));
        formatter.put(TemplateLocalDate.class, new DateFormatter("yyyy-MM-dd"));
        formatter.put(TemplateLocalTime.class, new TimeFormatter("hh:mm:ss", ZoneOffset.UTC));
        formatter.put(TemplateDuration.class, new DurationFormatter());
        formatter.put(TemplatePeriod.class, new DurationFormatter());
        formatter.put(TemplateMonthDay.class, new DateTimeFormatter("MM-dd"));
        formatter.put(TemplateYearMonth.class, new DateTimeFormatter("yyyy-MM"));
        return formatter;
    }

    @Override
    public Map<Class<?>, TypeMapper> providerTypeMapper() {
        Map<Class<?>, TypeMapper> mapper = new HashMap<>();
        mapper.put(Instant.class, o -> new TemplateInstant((Instant) o));
        mapper.put(ZonedDateTime.class, o -> new TemplateZonedDateTime((ZonedDateTime) o));
        mapper.put(LocalDateTime.class, o -> new TemplateLocalDateTime((LocalDateTime) o));
        mapper.put(LocalDate.class, o -> new TemplateLocalDate((LocalDate) o));
        mapper.put(LocalTime.class, o -> new TemplateLocalTime((LocalTime) o));
        mapper.put(Duration.class, o -> new TemplateDuration((Duration) o));
        mapper.put(Period.class, o -> new TemplatePeriod((Period) o));
        mapper.put(Year.class, o -> new TemplateYear((Year) o));
        mapper.put(MonthDay.class, o -> new TemplateMonthDay((MonthDay) o));
        mapper.put(YearMonth.class, o -> new TemplateYearMonth((YearMonth) o));
        return mapper;
    }
}
