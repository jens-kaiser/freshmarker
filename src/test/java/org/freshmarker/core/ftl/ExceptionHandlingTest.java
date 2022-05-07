package org.freshmarker.core.ftl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ftl.ParseException;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import org.freshmarker.Configuration;
import org.freshmarker.Template;
import org.freshmarker.core.StringTemplateLoader;
import org.freshmarker.core.UnsupportedBuiltInException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class ExceptionHandlingTest {

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
      "test: ${true?}",
      "test: ${false?string('ja','nein')",
  })
  void parseError(String templateSource) {
    templateLoader.putTemplate("test", templateSource);
    assertThrows(ParseException.class, () -> configuration.getTemplate("test"));
  }

  @ParameterizedTest
  @CsvSource(value = {
      "test: ${true?upper_case}",
  })
  void builtInTypeError(String templateSource) throws IOException, ParseException {
    templateLoader.putTemplate("test", templateSource);
    Template template = configuration.getTemplate("test");
    Map<String, Object> dataModel = Map.of();
    UnsupportedBuiltInException exception = assertThrows(UnsupportedBuiltInException.class, () -> template.process(dataModel));
    assertEquals("unsupported builtin 'upper_case' for TemplateBoolean in line 1 column 7 '${true?upper_case}'", exception.getMessage());
  }
}
