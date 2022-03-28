package org.freshmarker.core.ftl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ftl.ParseException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.freshmarker.Configuration;
import org.freshmarker.Template;
import org.freshmarker.core.StringTemplateLoader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class LoopDirectiveTest {

  private Configuration configuration;
  private StringTemplateLoader templateLoader;

  @BeforeEach
  public void setUp() {
    configuration = new Configuration();
    templateLoader = new StringTemplateLoader();
    configuration.registerTemplateLoader(templateLoader);
  }

  @Test
  void loop() throws ParseException, IOException {
    templateLoader.putTemplate("test",
        "test: <#list sequence as s>${s?index}. ${s}\n</#list>");
    Template template = configuration.getTemplate("test");
    assertEquals("test: 0. a\n1. b\n2. c\n3. d\n", template.process(Map.of("sequence", List.of("a","b","c","d"))));
  }

  @Test
  void loopLoop() throws ParseException, IOException {
    templateLoader.putTemplate("test",
        "test: <#list sequence as s><#list sequence as t>(${s?index}.${t?index})</#list></#list>");
    Template template = configuration.getTemplate("test");
    assertEquals("test: (0.0)(0.1)(0.2)(0.3)(1.0)(1.1)(1.2)(1.3)(2.0)(2.1)(2.2)(2.3)(3.0)(3.1)(3.2)(3.3)", template.process(Map.of("sequence", List.of("a","b","c","d"))));
  }
  @Test
  void firstLast() throws ParseException, IOException {
    templateLoader.putTemplate("test",
        "test: <#list sequence as s>(${s?is_first}.${s?is_last})</#list>");
    Template template = configuration.getTemplate("test");
    assertEquals("test: (yes.no)(no.no)(no.no)(no.yes)", template.process(Map.of("sequence", List.of("a","b","c","d"))));
  }
}