package org.freshmarker.core.ftl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ftl.ParseException;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import org.freshmarker.Configuration;
import org.freshmarker.Template;
import org.freshmarker.core.StringTemplateLoader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class OutputFormatTest {
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
      "HTML,<>\"',test: &lt;&gt;&quot;&#39;",
      "XHTML,<>\"',test: &lt;&gt;&quot;&#39;",
      "XML,<>\"',test: &lt;&gt;&quot;&apos;",
      "undefined,<>\"',test: <>\"'",
      "plainText,<>\"',test: <>\"'",
      "JavaScript,<>\"',test: <>\"'",
      "JSON,<>\"',test: <>\"'",
      "CSS,<>\"',test: <>\"'",
  })
  void interpolation(String format, String content, String expected) throws ParseException, IOException {
    templateLoader.putTemplate("test", "test: ${content}");
    configuration.setOutputFormat(format);
    Template template = configuration.getTemplate("test");
    assertEquals(expected, template.process(Map.of("content", content)));
  }

  @Test
  void htmlOutputFormatBlock() throws ParseException, IOException {
    templateLoader.putTemplate("test", "test: ${content}<#outputformat 'HTML'>${content}</#outputformat>${content}");
    Template template = configuration.getTemplate("test");
    assertEquals("test: <>\"'&lt;&gt;&quot;&#39;<>\"'", template.process(Map.of("content", "<>\"'")));
  }

  @ParameterizedTest
  @CsvSource({
      "HTML,<>\"',test: <>\"'",
      "XHTML,<>\"',test: <>\"'",
      "XML,<>\"',test: <>\"'",
      "undefined,<>\"',test: <>\"'",
      "plainText,<>\"',test: <>\"'",
      "JavaScript,<>\"',test: <>\"'",
      "JSON,<>\"',test: <>\"'",
      "CSS,<>\"',test: <>\"'",
  })
  void unescapeInterpolation(String format, String content, String expected) throws ParseException, IOException {
    templateLoader.putTemplate("test", "test: ${content?noEsc}");
    configuration.setOutputFormat(format);
    Template template = configuration.getTemplate("test");
    assertEquals(expected, template.process(Map.of("content", content)));
  }
}
