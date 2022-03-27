package org.freshmarker.core.ftl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ftl.ParseException;
import java.io.IOException;
import java.util.Map;
import org.freshmarker.Configuration;
import org.freshmarker.Template;
import org.freshmarker.core.StringTemplateLoader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class SwitchDirectiveTest {

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
  void switchCaseDefault(String text, String expected) throws ParseException, IOException {
    templateLoader.putTemplate("test",
        "test: <#switch text><#case 'AAA'>${text}1<#case 'BBB'>${text}2<#default>${text}3</#switch>");
    Template template = configuration.getTemplate("test");
    assertEquals(expected, template.process(Map.of("text", text)));
  }

  @ParameterizedTest
  @CsvSource({
      "AAA, test: AAA1",
      "BBB, test: BBB2",
      "CCC, 'test: '",
  })
  void switchCase(String text, String expected) throws ParseException, IOException {
    templateLoader.putTemplate("test",
        "test: <#switch text><#case 'AAA'>${text}1<#case 'BBB'>${text}2</#switch>");
    Template template = configuration.getTemplate("test");
    assertEquals(expected, template.process(Map.of("text", text)));
  }

  @ParameterizedTest
  @CsvSource({
      "AAA, test: AAA1",
      "BBB, test: BBB3",
      "CCC, test: CCC3",
  })
  void switchDefault(String text, String expected) throws ParseException, IOException {
    templateLoader.putTemplate("test",
        "test: <#switch text><#case 'AAA'>${text}1<#default>${text}3</#switch>");
    Template template = configuration.getTemplate("test");
    assertEquals(expected, template.process(Map.of("text", text)));
  }
}