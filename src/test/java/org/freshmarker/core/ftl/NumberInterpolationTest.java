package org.freshmarker.core.ftl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ftl.ParseException;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import org.freshmarker.core.StringTemplateLoader;
import org.freshmarker.Configuration;
import org.freshmarker.Template;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class NumberInterpolationTest {
  private Configuration configuration;
  private StringTemplateLoader templateLoader;

  @BeforeEach
  public void setUp() {
    configuration = new Configuration();
    configuration.setLocale(Locale.GERMANY);
    templateLoader = new StringTemplateLoader();
    configuration.registerTemplateLoader(templateLoader);
  }

  @ParameterizedTest
  @CsvSource(value = {
      "test: ${42};test: 42",
      "test: ${42.23};test: 42,23",
  }, delimiterString = ";")
  void interpolationConstant(String templateSource, String expected) throws ParseException, IOException {
    templateLoader.putTemplate("test", templateSource);
    Template template = configuration.getTemplate("test");
    assertEquals(expected, template.process(Map.of()));
  }

  @ParameterizedTest
  @CsvSource(value = {
      "test: ${42*x};test: 1.764",
      "test: ${42.0*x};test: 1.764",
      "test: ${42*10-0.5};test: 419,5",
      "test: ${42.23*10};test: 422,3",
      "test: ${x*x};test: 1.764",
      "test: ${x % 4};test: 2",
  }, delimiterString = ";")
  void interpolationExpression(String templateSource, String expected) throws ParseException, IOException {
    templateLoader.putTemplate("test", templateSource);
    Template template = configuration.getTemplate("test");
    assertEquals(expected, template.process(Map.of("x", (byte)42)));
  }
}