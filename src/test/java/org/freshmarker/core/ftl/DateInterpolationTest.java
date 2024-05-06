package org.freshmarker.core.ftl;

import ftl.ParseException;
import org.freshmarker.Configuration;
import org.freshmarker.Template;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DateInterpolationTest {

    private static final GregorianCalendar CALENDAR = new GregorianCalendar(1968, Calendar.AUGUST, 24, 12, 30, 45);

    private Configuration configuration;

    @BeforeEach
    public void setUp() {
        configuration = new Configuration();
    }

    @Test
    void interpolationDate() throws ParseException {
        Template template = configuration.getTemplate("test", "test: ${temporal}");
        String result = template.process(Map.of("temporal", new java.sql.Date(CALENDAR.getTimeInMillis())));
        assertEquals("test: 1968-08-24", result);
    }

    @Test
    void interpolationDateComputerAudience() throws ParseException {
        Template template = configuration.getTemplate("test", "test: ${temporal?c}");
        String result = template.process(Map.of("temporal", new java.sql.Date(CALENDAR.getTimeInMillis())));
        assertEquals("test: 1968-08-24", result);
    }

    @Test
    void interpolationTime() throws ParseException {
        Template template = configuration.getTemplate("test", "test: ${temporal}");
        CALENDAR.getTime();
        String result = template.process(Map.of("temporal", new java.sql.Time(CALENDAR.getTimeInMillis())));
        assertEquals("test: 12:30:45", result);
    }

    @Test
    void interpolationTimeComputerAudience() throws ParseException {
        Template template = configuration.getTemplate("test", "test: ${temporal?c}");
        CALENDAR.getTime();
        String result = template.process(Map.of("temporal", new java.sql.Time(CALENDAR.getTimeInMillis())));
        assertEquals("test: 12:30:45", result);
    }

    @Test
    void interpolationDateTime() throws ParseException {
        Template template = configuration.getTemplate("test", "test: ${temporal}");
        String result = template.process(Map.of("temporal", CALENDAR.getTime()));
        assertEquals("test: 1968-08-24 12:30:45", result);
    }

    @Test
    void interpolationDateTimeComputerAudience() throws ParseException {
        Template template = configuration.getTemplate("test", "test: ${temporal?c}");
        String result = template.process(Map.of("temporal", CALENDAR.getTime()));
        assertEquals("test: 1968-08-24T12:30:45", result);
    }

    @Test
    void interpolationDateTimeToDate() throws ParseException {
        Template template = configuration.getTemplate("test", "test: ${temporal?date}");
        String result = template.process(Map.of("temporal", CALENDAR.getTime()));
        assertEquals("test: 1968-08-24", result);
    }

    @Test
    void interpolationDateTimeToTime() throws ParseException {
        Template template = configuration.getTemplate("test", "test: ${temporal?time}");
        String result = template.process(Map.of("temporal", CALENDAR.getTime()));
        assertEquals("test: 12:30:45", result);
    }
}

