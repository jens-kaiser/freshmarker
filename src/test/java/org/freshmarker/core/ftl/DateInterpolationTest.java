package org.freshmarker.core.ftl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ftl.ParseException;
import java.io.IOException;
import java.sql.Time;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Map;
import org.freshmarker.Configuration;
import org.freshmarker.Template;
import org.freshmarker.core.StringTemplateLoader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DateInterpolationTest {

  private static final GregorianCalendar CALENDAR = new GregorianCalendar(1968, Calendar.AUGUST, 24, 12, 30, 45);

  private Configuration configuration;
  private StringTemplateLoader templateLoader;

  @BeforeEach
  public void setUp() {
    configuration = new Configuration();
    templateLoader = new StringTemplateLoader();
    configuration.registerTemplateLoader(templateLoader);
  }

  @Test
  void interpolationDate() throws ParseException, IOException {
    templateLoader.putTemplate("test", "test: ${temporal}");
    Template template = configuration.getTemplate("test");
    String result = template.process(Map.of("temporal", new java.sql.Date(CALENDAR.getTimeInMillis())));
    assertEquals("test: 1968-08-24", result);
  }

  @Test
  void interpolationDateComputerAudience() throws ParseException, IOException {
    templateLoader.putTemplate("test", "test: ${temporal?c}");
    Template template = configuration.getTemplate("test");
    String result = template.process(Map.of("temporal", new java.sql.Date(CALENDAR.getTimeInMillis())));
    assertEquals("test: 1968-08-24", result);
  }

  @Test
  void interpolationTime() throws ParseException, IOException {
    templateLoader.putTemplate("test", "test: ${temporal}");
    Template template = configuration.getTemplate("test");
    CALENDAR.getTime();
    String result = template.process(Map.of("temporal", new java.sql.Time(CALENDAR.getTimeInMillis())));
    assertEquals("test: 12:30:45", result);
  }

  @Test
  void interpolationTimeComputerAudience() throws ParseException, IOException {
    templateLoader.putTemplate("test", "test: ${temporal?c}");
    Template template = configuration.getTemplate("test");
    CALENDAR.getTime();
    String result = template.process(Map.of("temporal", new java.sql.Time(CALENDAR.getTimeInMillis())));
    assertEquals("test: 12:30:45", result);
  }

  @Test
  void interpolationDateTime() throws ParseException, IOException {
    templateLoader.putTemplate("test", "test: ${temporal}");
    Template template = configuration.getTemplate("test");
    String result = template.process(Map.of("temporal", CALENDAR.getTime()));
    assertEquals("test: 1968-08-24 12:30:45", result);
  }

  @Test
  void interpolationDateTimeComputerAudience() throws ParseException, IOException {
    templateLoader.putTemplate("test", "test: ${temporal?c}");
    Template template = configuration.getTemplate("test");
    String result = template.process(Map.of("temporal", CALENDAR.getTime()));
    assertEquals("test: 1968-08-24T12:30:45", result);
  }

  @Test
  void interpolationDateTimeToDate() throws ParseException, IOException {
    templateLoader.putTemplate("test", "test: ${temporal?date}");
    Template template = configuration.getTemplate("test");
    String result = template.process(Map.of("temporal", CALENDAR.getTime()));
    assertEquals("test: 1968-08-24", result);
  }

  @Test
  void interpolationDateTimeToTime() throws ParseException, IOException {
    templateLoader.putTemplate("test", "test: ${temporal?time}");
    Template template = configuration.getTemplate("test");
    String result = template.process(Map.of("temporal", CALENDAR.getTime()));
    assertEquals("test: 12:30:45", result);
  }
}

