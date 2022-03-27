package org.freshmarker.core.ftl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ftl.ParseException;
import java.io.IOException;
import java.util.Map;
import org.freshmarker.core.StringTemplateLoader;
import org.freshmarker.Configuration;
import org.freshmarker.Template;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class IfDirectiveTest {

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
      "AAA, test: AAA1",
      "BBB, test: BBB2",
      "CCC, test: CCC3",
  })
  void ifElseifElse(String text, String expected) throws ParseException, IOException {
    templateLoader.putTemplate("test",
        "test: <#if text?contains('A')>${text}1<#elseif text?contains('BB')>${text}2<#else>${text}3</#if>");
    Template template = configuration.getTemplate("test");
    assertEquals(expected, template.process(Map.of("text", text)));
  }

  @ParameterizedTest
  @CsvSource({
      "AAA, test: AAA1",
      "BBB, test: BBB2",
      "CCC, 'test: '",
  })
  void ifElseif(String text, String expected) throws ParseException, IOException {
    templateLoader.putTemplate("test",
        "test: <#if text?contains('A')>${text}1<#elseif text?contains('BB')>${text}2</#if>");
    Template template = configuration.getTemplate("test");
    assertEquals(expected, template.process(Map.of("text", text)));
  }

  @ParameterizedTest
  @CsvSource({
      "AAA, test: AAA1",
      "BBB, test: BBB3",
      "CCC, test: CCC3",
  })
  void ifElse(String text, String expected) throws ParseException, IOException {
    templateLoader.putTemplate("test",
        "test: <#if text?contains('A')>${text}1<#else>${text}3</#if>");
    Template template = configuration.getTemplate("test");
    assertEquals(expected, template.process(Map.of("text", text)));
  }
}