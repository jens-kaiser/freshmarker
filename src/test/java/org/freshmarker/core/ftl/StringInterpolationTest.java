package org.freshmarker.core.ftl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ftl.ParseException;
import java.io.IOException;
import java.util.Map;
import org.freshmarker.core.StringTemplateLoader;
import org.freshmarker.Configuration;
import org.freshmarker.Template;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class StringInterpolationTest {

  private static final String TEXT = "The lazy Dog jumps over the Quick brown Fox";

  private Configuration configuration;
  private StringTemplateLoader templateLoader;

  @BeforeEach
  public void setUp() {
    configuration = new Configuration();
    templateLoader = new StringTemplateLoader();
    configuration.registerTemplateLoader(templateLoader);
  }

  @ParameterizedTest
  @CsvSource({
      "test: ${text},test: The lazy Dog jumps over the Quick brown Fox",
      "test: ${text?upper_case},test: THE LAZY DOG JUMPS OVER THE QUICK BROWN FOX",
      "test: ${text?lower_case},test: the lazy dog jumps over the quick brown fox",
      "test: ${text?upper_case?lower_case},test: the lazy dog jumps over the quick brown fox"
  })
  void interpolationString(String templateSource, String expected) throws ParseException, IOException {
    templateLoader.putTemplate("test", templateSource);
    Template template = configuration.getTemplate("test");
    assertEquals(expected, template.process(Map.of("text", TEXT)));
  }

  @Test
  void interpolationBoolean() throws ParseException, IOException {
    templateLoader.putTemplate("test", "test: ${text?boolean}");
    Template template = configuration.getTemplate("test");
    assertEquals("test: yes", template.process(Map.of("text", "true")));
  }

  @Test
  void interpolationTrim() throws ParseException, IOException {
    templateLoader.putTemplate("test", "test: ${text?trim}");
    Template template = configuration.getTemplate("test");
    assertEquals("test: text", template.process(Map.of("text", "  text  ")));
  }

  @Test
  void interpolationLength() throws ParseException, IOException {
    templateLoader.putTemplate("test", "test: ${text?length} ${text?trim?length}");
    Template template = configuration.getTemplate("test");
    assertEquals("test: 8 4", template.process(Map.of("text", "  text  ")));
  }

  @ParameterizedTest
  @CsvSource({
      "test: ${text?contains('ex')},test: yes",
      "test: ${text?contains('EX')},test: no",
      "test: ${text?ends_with('xt')},test: yes",
      "test: ${text?ends_with('XT')},test: no",
  })
  void interpolationContainsAndEndWith(String templateSource, String expected) throws ParseException, IOException {
    templateLoader.putTemplate("test", templateSource);
    Template template = configuration.getTemplate("test");
    assertEquals(expected, template.process(Map.of("text", "text")));
  }

  @ParameterizedTest
  @CsvSource({
      "test: ${text[2..3]},test: CD",
      "test: ${text[2..]},test: CDEF",
  })
  void interpolationSlices(String templateSource, String expected) throws ParseException, IOException {
    templateLoader.putTemplate("test", templateSource);
    Template template = configuration.getTemplate("test");
    assertEquals(expected, template.process(Map.of("text", "ABCDEF")));
  }
}