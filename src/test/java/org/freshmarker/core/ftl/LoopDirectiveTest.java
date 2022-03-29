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
  void loopIndex() throws ParseException, IOException {
    templateLoader.putTemplate("test",
        "test: <#list sequence as s>${s?index}. ${s}\n</#list>");
    Template template = configuration.getTemplate("test");
    assertEquals("test: 0. a\n1. b\n2. c\n3. d\n", template.process(Map.of("sequence", List.of("a","b","c","d"))));
  }

  @Test
  void hasNext() throws ParseException, IOException {
    templateLoader.putTemplate("test",
        "test: <#list sequence as s>${s?has_next} </#list>");
    Template template = configuration.getTemplate("test");
    assertEquals("test: yes no ", template.process(Map.of("sequence", List.of("a","b"))));
  }

  @Test
  void itemParity() throws ParseException, IOException {
    templateLoader.putTemplate("test",
        "test: <#list sequence as s>${s?item_parity} </#list>");
    Template template = configuration.getTemplate("test");
    assertEquals("test: odd even odd even ", template.process(Map.of("sequence", List.of("a","b","c","d"))));
  }

  @Test
  void itemParityCap() throws ParseException, IOException {
    templateLoader.putTemplate("test",
        "test: <#list sequence as s>${s?item_parity_cap} </#list>");
    Template template = configuration.getTemplate("test");
    assertEquals("test: Odd Even Odd Even ", template.process(Map.of("sequence", List.of("a","b","c","d"))));
  }

  @Test
  void itemCycle() throws ParseException, IOException {
    templateLoader.putTemplate("test",
        "test: <#list sequence as s>${s?item_cycle(1, 2, 3)} </#list>");
    Template template = configuration.getTemplate("test");
    assertEquals("test: 1 2 3 1 ", template.process(Map.of("sequence", List.of("a","b","c","d"))));
  }

  @Test
  void firstLast() throws ParseException, IOException {
    templateLoader.putTemplate("test",
        "test: <#list sequence as s>(${s?is_first}.${s?is_last})</#list>");
    Template template = configuration.getTemplate("test");
    assertEquals("test: (yes.no)(no.no)(no.no)(no.yes)", template.process(Map.of("sequence", List.of("a","b","c","d"))));
  }
}