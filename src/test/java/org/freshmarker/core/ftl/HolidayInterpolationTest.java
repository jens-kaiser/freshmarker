package org.freshmarker.core.ftl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ftl.ParseException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;
import java.util.Locale;
import java.util.Map;
import org.freshmarker.Configuration;
import org.freshmarker.Template;
import org.freshmarker.core.StringTemplateLoader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class HolidayInterpolationTest {

  private Configuration configuration;
  private StringTemplateLoader templateLoader;

  @BeforeEach
  public void setUp() {
    configuration = new Configuration();
    configuration.setLocale(Locale.UK);
    templateLoader = new StringTemplateLoader();
    configuration.registerTemplateLoader(templateLoader);
  }

  @Test
  void interpolationIsHoliday() throws ParseException, IOException {
    templateLoader.putTemplate("test", "holiday: ${temporal?is_holiday}");
    Template template = configuration.getTemplate("test");
    String result = template.process(Map.of("temporal", LocalDate.of(2022, Month.DECEMBER, 25)));
    assertEquals("holiday: yes", result);
  }

  @Test
  void interpolationGetHolidayEn() throws ParseException, IOException {
    templateLoader.putTemplate("test", "holiday: ${temporal?get_holiday}");
    Template template = configuration.getTemplate("test");
    String result = template.process(Map.of("temporal", LocalDate.of(2022, Month.DECEMBER, 25)));
    assertEquals("holiday: Christmas Day", result);
  }

  @Test
  void interpolationGetHolidayDe() throws ParseException, IOException {
    configuration.setLocale(Locale.GERMANY);
    templateLoader.putTemplate("test", "holiday: ${temporal?get_holiday}");
    Template template = configuration.getTemplate("test");
    String result = template.process(Map.of("temporal", LocalDate.of(2022, Month.DECEMBER, 25)));
    assertEquals("holiday: 1. Weihnachtstag", result);
  }

  @Test
  void interpolationGetHolidayDeSetting() throws ParseException, IOException {
    templateLoader.putTemplate("test", "holiday: ${temporal?get_holiday}<#setting locale='de-DE'> - ${temporal?get_holiday}");
    Template template = configuration.getTemplate("test");
    String result = template.process(Map.of("temporal", LocalDate.of(2022, Month.DECEMBER, 25)));
    assertEquals("holiday: Christmas Day - 1. Weihnachtstag", result);
  }

  @Test
  void interpolationGetHolidayWithFallback() throws ParseException, IOException {
    templateLoader.putTemplate("test", "holiday: ${temporal?get_holiday('no holiday')}");
    Template template = configuration.getTemplate("test");
    String result = template.process(Map.of("temporal", LocalDate.of(1968, Month.AUGUST, 24)));
    assertEquals("holiday: no holiday", result);
  }
}

