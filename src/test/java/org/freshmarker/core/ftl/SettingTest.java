package org.freshmarker.core.ftl;

import ftl.ParseException;
import org.freshmarker.Configuration;
import org.freshmarker.Template;
import org.freshmarker.core.ProcessException;
import org.freshmarker.core.StringTemplateLoader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SettingTest {
  private Configuration configuration;

  @BeforeEach
  public void setUp() {
    configuration = new Configuration();
    configuration.setLocale(Locale.GERMANY);
  }

  @ParameterizedTest
  @CsvSource(value = "test: ${42.23} - <#setting locale=\"en_US\">${42.23};test: 42,23 - 42.23", delimiterString = ";")
  void setting(String templateSource, String expected) throws ParseException, IOException {
    Template template = configuration.getTemplate("test", templateSource);
    assertEquals(expected, template.process(Map.of()));
  }
}