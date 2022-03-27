package org.freshmarker.core.ftl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ftl.ParseException;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import org.freshmarker.core.StringTemplateLoader;
import org.freshmarker.Configuration;
import org.freshmarker.Template;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TemporalInterpolationTest {

  private Configuration configuration;
  private StringTemplateLoader templateLoader;

  @BeforeEach
  public void setUp() {
    configuration = new Configuration();
    templateLoader = new StringTemplateLoader();
    configuration.registerTemplateLoader(templateLoader);
  }

  @Test
  void interpolationLocalDate() throws ParseException, IOException {
    templateLoader.putTemplate("test", "test: ${temporal}");
    Template template = configuration.getTemplate("test");
    String result = template.process(Map.of("temporal", LocalDate.of(1968, Month.AUGUST, 24)));
    assertEquals("test: 1968-08-24", result);
  }

  @Test
  void interpolationLocalTime() throws ParseException, IOException {
    templateLoader.putTemplate("test", "test: ${temporal}");
    Template template = configuration.getTemplate("test");
    String result = template.process(Map.of("temporal", LocalTime.of(12, 30, 45)));
    assertEquals("test: 12:30:45", result);
  }

  @Test
  void interpolationLocalDateTime() throws ParseException, IOException {
    templateLoader.putTemplate("test", "test: ${temporal}");
    Template template = configuration.getTemplate("test");
    String result = template.process(Map.of("temporal", LocalDateTime.of(1968, Month.AUGUST, 24, 12, 30, 45)));
    assertEquals("test: 1968-08-24 12:30:45", result);
  }

  @Test
  void interpolationLocalDateTimeComputerAudience() throws ParseException, IOException {
    templateLoader.putTemplate("test", "test: ${temporal?c}");
    Template template = configuration.getTemplate("test");
    String result = template.process(Map.of("temporal", LocalDateTime.of(1968, Month.AUGUST, 24, 12, 30, 45)));
    assertEquals("test: 1968-08-24T12:30:45", result);
  }

  @Test
  void interpolationLocalDateTimeToLocalDate() throws ParseException, IOException {
    templateLoader.putTemplate("test","test: ${temporal?date}");
    Template template = configuration.getTemplate("test");
    String result = template.process(Map.of("temporal", LocalDateTime.of(1968, Month.AUGUST, 24, 12, 30, 45)));
    assertEquals("test: 1968-08-24", result);
  }

  @Test
  void interpolationLocalDateTimeToLocalTime() throws ParseException, IOException {
    templateLoader.putTemplate("test", "test: ${temporal?time}");
    Template template = configuration.getTemplate("test");
    String result = template.process(Map.of("temporal", LocalDateTime.of(1968, Month.AUGUST, 24, 12, 30, 45)));
    assertEquals("test: 12:30:45", result);
  }

  @Test
  void interpolationDuration() throws ParseException, IOException {
    templateLoader.putTemplate("test", "test: ${temporal}");
    Template template = configuration.getTemplate("test");
    String result = template.process(Map.of("temporal", Duration.of(43, ChronoUnit.MINUTES)));
    assertEquals("test: PT43M", result);
  }

  @Test
  void interpolationPeriod() throws ParseException, IOException {
    templateLoader.putTemplate("test", "test: ${temporal}");
    Template template = configuration.getTemplate("test");
    String result = template.process(Map.of("temporal", Period.of(2, 4, 1)));
    assertEquals("test: P2Y4M1D", result);
  }
}

