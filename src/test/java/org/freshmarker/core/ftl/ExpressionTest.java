package org.freshmarker.core.ftl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ftl.ParseException;
import java.io.IOException;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import org.freshmarker.Configuration;
import org.freshmarker.Template;
import org.freshmarker.core.StringTemplateLoader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class ExpressionTest {

  private Configuration configuration;
  private StringTemplateLoader templateLoader;

  @BeforeEach
  public void setUp() {
    configuration = new Configuration();
    templateLoader = new StringTemplateLoader();
    configuration.registerTemplateLoader(templateLoader);
  }

  @Test
  void stringConcat() throws ParseException, IOException {
    templateLoader.putTemplate("test", "test: ${('abcdefg' + 'hijklmnop' + 'qrstuvwxyz')?upper_case}");
    Template template = configuration.getTemplate("test");
    assertEquals("test: ABCDEFGHIJKLMNOPQRSTUVWXYZ", template.process(Map.of()));
  }

  @Test
  void stringConcatWithVars() throws ParseException, IOException {
    templateLoader.putTemplate("test", "test: ${(prefix + 'hijklmnop' + suffix)?upper_case}");
    Template template = configuration.getTemplate("test");
    assertEquals("test: ABCDEFGHIJKLMNOPQRSTUVWXYZ", template.process(Map.of("prefix", "abcdefg", "suffix", "qrstuvwxyz")));
  }

  @Test
  void stringConcatWithEmptyVars() throws ParseException, IOException {
    templateLoader.putTemplate("test", "test: ${(prefix + 'hijklmnop' + suffix)?upper_case}");
    Template template = configuration.getTemplate("test");
    assertEquals("test: HIJKLMNOP", template.process(Map.of("prefix", "", "suffix", "")));
  }

  @ParameterizedTest
  @CsvSource({
      "2 > 1, true",
      "1 > 2, false",
      "3 >= 1, true",
      "1 >= 3, false",
      "2 < 1, false",
      "1 < 2, true",
      "3 <= 1, false",
      "1 <= 3, true",
      "2 gt 1, true",
      "1 gt 2, false",
      "3 gte 1, true",
      "1 gte 3, false",
      "2 lt 1, false",
      "1 lt 2, true",
      "3 lte 1, false",
      "1 lte 3, true",
  })
  void numberRelation(String expression, boolean result) throws ParseException, IOException {
    templateLoader.putTemplate("test", "test: ${(" + expression + ")?c}");
    Template template = configuration.getTemplate("test");
    assertEquals("test: " + result, template.process(Map.of("prefix", "", "suffix", "")));
  }

  @ParameterizedTest
  @CsvSource({
      "1 == 1, true",
      "1 == 2, false",
      "1 = 1, true",
      "1 = 2, false",
      "1 != 1, false",
      "1 != 2, true",
  })
  void primitiveEquality(String expression, boolean result) throws ParseException, IOException {
    templateLoader.putTemplate("test", "test: ${(" + expression + ")?c}");
    Template template = configuration.getTemplate("test");
    assertEquals("test: " + result, template.process(Map.of("prefix", "", "suffix", "")));
  }

  @ParameterizedTest
  @CsvSource({
      "4 == (test?ordinal), true",
      "4 != (test?ordinal), false",
      "4 <= (test?ordinal), true",
      "3 < (test?ordinal), true",
      "4 >= (test?ordinal), true",
      "5 > (test?ordinal), true",
      "4 lte (test?ordinal), true",
      "3 lt (test?ordinal), true",
      "4 gte (test?ordinal), true",
      "5 gt (test?ordinal), true"
  })
  void relationWithEnum(String expression, boolean result) throws ParseException, IOException {
    templateLoader.putTemplate("test", "test: ${(" + expression + ")?c}");
    Template template = configuration.getTemplate("test");
    assertEquals("test: " + result, template.process(Map.of("test", StandardOpenOption.CREATE)));
  }
}
