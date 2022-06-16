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
import org.freshmarker.core.WrongTypeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

  @Test
  void builtInTypeError() throws IOException, ParseException {
    templateLoader.putTemplate("test", "test: ${true?upper_case}");
    Template template = configuration.getTemplate("test");
    Map<String, Object> dataModel = Map.of();
    UnsupportedBuiltInException exception = assertThrows(UnsupportedBuiltInException.class,
        () -> template.process(dataModel));
    assertEquals("unsupported builtin 'upper_case' for TemplateBoolean at test:1:7 '${true?upper_case}'",
        exception.getMessage());
  }

  @Test
  void wrongTypeError() throws IOException, ParseException {
    templateLoader.putTemplate("test", "test: ${!test}");
    Template template = configuration.getTemplate("test");
    Map<String, Object> dataModel = Map.of("test", 42);
    WrongTypeException exception = assertThrows(WrongTypeException.class, () -> template.process(dataModel));
    assertEquals("expected TemplateBoolean but is TemplateNumber (org.freshmarker.core.model.number.IntegerNumber@49) at test:1:7 '${!test}'",
        exception.getMessage());
  }

  @Test
  void ifConditionError() throws IOException, ParseException {
    templateLoader.putTemplate("test",
        "test: \n" +
            "<#if text?contains('A')>\n" +
            "${text}1\n" +
            "<#elseif text?contains('BB')>\n" +
            "${text}2\n" +
            "<#else>\n" +
            "${text}3\n" +
            "</#if>");
    Template template = configuration.getTemplate("test");
    Map<String, Object> dataModel = Map.of("text", 42);
    UnsupportedBuiltInException exception = assertThrows(UnsupportedBuiltInException.class,
        () -> template.process(dataModel));
    assertEquals("unsupported builtin 'contains' for TemplateNumber at test:2:6 'text?contains('A')'",
        exception.getMessage());
  }

  @Test
  void ifBlockError() throws IOException, ParseException {
    templateLoader.putTemplate("test",
        "test:\n" +
            "<#if text?contains('A')>\n" +
            "${text?xxx}1\n" +
            "<#elseif text?contains('BB')>\n" +
            "${text}2\n<#else>${text}3\n" +
            "</#if>");
    Template template = configuration.getTemplate("test");
    Map<String, Object> dataModel = Map.of("text", "A");
    UnsupportedBuiltInException exception = assertThrows(UnsupportedBuiltInException.class,
        () -> template.process(dataModel));
    assertEquals("unsupported builtin 'xxx' for TemplateString at test:3:1 '${text?xxx}'",
        exception.getMessage());
  }

  @Test
  void switchExpressionError() throws IOException, ParseException {
    templateLoader.putTemplate("test",
        "test:\n<#switch text?upper_case>\n<#case 'AAA'>${text}1\n<#case 'BBB'>${text}2\n</#switch>");
    Template template = configuration.getTemplate("test");
    Map<String, Object> dataModel = Map.of("text", 42);
    UnsupportedBuiltInException exception = assertThrows(UnsupportedBuiltInException.class,
        () -> template.process(dataModel));
    assertEquals("unsupported builtin 'upper_case' for TemplateNumber at test:2:10 'text?upper_case'",
        exception.getMessage());
  }
}
