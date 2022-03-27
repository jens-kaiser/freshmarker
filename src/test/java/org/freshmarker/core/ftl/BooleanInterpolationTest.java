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

class BooleanInterpolationTest {
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
  @CsvSource({
      "test: ${true},test: yes",
      "test: ${false},test: no",
  })
  void interpolationConstant(String templateSource, String expected) throws ParseException, IOException {
    templateLoader.putTemplate("test", templateSource);
    Template template = configuration.getTemplate("test");
    assertEquals(expected, template.process(Map.of()));
  }

  @ParameterizedTest
  @CsvSource(value = {
      "test: ${true?string('ja','nein')};test: ja",
      "test: ${false?string('ja','nein')};test: nein",
      "test: ${true?then(text,'nein')};test: test",
      "test: ${false?then('ja',text)};test: test",
      "test: ${var?then(text,'nein')};test: test",
      "test: ${(!var)?then('ja',text)};test: test",
  }, delimiterString = ";")
  void interpolationBuildIn(String templateSource, String expected) throws ParseException, IOException {
    templateLoader.putTemplate("test", templateSource);
    Template template = configuration.getTemplate("test");
    assertEquals(expected, template.process(Map.of("var", true, "text", "test")));
  }
}