package org.freshmarker.core.ftl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ftl.ParseException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.freshmarker.Configuration;
import org.freshmarker.Template;
import org.freshmarker.core.ProcessException;
import org.freshmarker.core.StringTemplateLoader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ListDirectiveTest {

  private Configuration configuration;
  private StringTemplateLoader templateLoader;

  @BeforeEach
  public void setUp() {
    configuration = new Configuration();
    templateLoader = new StringTemplateLoader();
    configuration.registerTemplateLoader(templateLoader);
  }

  @Test
  void output() throws ParseException, IOException {
    Template template = configuration.getTemplate("test", "test: ${sequence}");
    assertThrows(ProcessException.class, () -> template.process(Map.of("sequence", List.of("a", "b", "c", "d"))));
  }

  @Test
  void loopIndex() throws ParseException, IOException {
    templateLoader.putTemplate("test",
            "test: <#list sequence as s, l>${l?index}. ${s}\n</#list>");
    Template template = configuration.getTemplate("test");
    assertEquals("test: 0. a\n1. b\n2. c\n3. d\n", template.process(Map.of("sequence", List.of("a", "b", "c", "d"))));
  }

  @Test
  void emptyList() throws ParseException, IOException {
    templateLoader.putTemplate("test",
        "test:\n<#list sequence as s, l>\n${l?index}. ${s}\n</#list>");
    Template template = configuration.getTemplate("test");
    assertEquals("test:\n", template.process(Map.of("sequence", List.of())));
  }
  @Test
  void hasNext() throws ParseException, IOException {
    templateLoader.putTemplate("test",
        "test: <#list sequence as s, l>${l?has_next} </#list>");
    Template template = configuration.getTemplate("test");
    assertEquals("test: yes no ", template.process(Map.of("sequence", List.of("a", "b"))));
  }

  @Test
  void itemParity() throws ParseException, IOException {
    templateLoader.putTemplate("test",
        "test: <#list sequence as s, l>${l?item_parity} </#list>");
    Template template = configuration.getTemplate("test");
    assertEquals("test: odd even odd even ", template.process(Map.of("sequence", List.of("a", "b", "c", "d"))));
  }

  @Test
  void itemParityCap() throws ParseException, IOException {
    templateLoader.putTemplate("test",
        "test: <#list sequence as s, l>${l?item_parity_cap} </#list>");
    Template template = configuration.getTemplate("test");
    assertEquals("test: Odd Even Odd Even ", template.process(Map.of("sequence", List.of("a", "b", "c", "d"))));
  }

  @Test
  void itemCycle() throws ParseException, IOException {
    templateLoader.putTemplate("test",
        "test: <#list sequence as s, l>${l?item_cycle(1, 2, 3)} </#list>");
    Template template = configuration.getTemplate("test");
    assertEquals("test: 1 2 3 1 ", template.process(Map.of("sequence", List.of("a", "b", "c", "d"))));
  }

  @Test
  void firstLast() throws ParseException, IOException {
    templateLoader.putTemplate("test", "test: <#list sequence as s, l>(${l?is_first}.${l?is_last})</#list>");
    Template template = configuration.getTemplate("test");
    assertEquals("test: (yes.no)(no.no)(no.no)(no.yes)",
        template.process(Map.of("sequence", List.of("a", "b", "c", "d"))));
  }

  @Test
  void range() throws ParseException, IOException {
    templateLoader.putTemplate("test", "test: <#list 1..4 as s>${s}</#list>");
    Template template = configuration.getTemplate("test");
    assertEquals("test: 1234", template.process(Map.of()));
  }

  public record Complex(String key, String value) {

  }

  @Test
  void recordLoop() throws ParseException, IOException {
    templateLoader.putTemplate("test",
        """
            test
              <#list sequence as s, l>
              ${l?index}. ${s.key} ${s.value}
            </#list>
            """);
    Template template = configuration.getTemplate("test");
    assertEquals("""
            test
              0. a b
              1. c d
            """,
        template.process(Map.of("sequence", List.of(new Complex("a", "b"), new Complex("c", "d")))));
  }

  @Test
  void whitespaceRemoval() throws ParseException, IOException {
    templateLoader.putTemplate("test",
        """
            test  \s
            
              <#list sequence as s, l> \s
              ${l?index}. ${s.key} ${s.value}
            </#list>   \s
            """);
    Template template = configuration.getTemplate("test");
    assertEquals("""
            test  \s
                   
              0. a b
              1. c d
            """,
        template.process(Map.of("sequence", List.of(new Complex("a", "b"), new Complex("c", "d")))));
  }

  @Test
  void additionalLineRemoval() throws ParseException, IOException {
    templateLoader.putTemplate("test",
        """
            test
            <#list sequence as s, l>
              ${l?index}. ${s.key} ${s.value}
            </#list>

            <#list sequence as s, l>
              ${l?index}. ${s.key} ${s.value}
            </#list>
            """);
    Template template = configuration.getTemplate("test");
    assertEquals("""
            test              
              0. a b
              1. c d

              0. a b
              1. c d
            """,
        template.process(Map.of("sequence", List.of(new Complex("a", "b"), new Complex("c", "d")))));
  }

  @Test
  void additionalLineRemoval2() throws ParseException, IOException {
    templateLoader.putTemplate("test",
        """
            <#list sequence as s, l>
              ${s.key} ${s.value}
            </#list>
            """);
    Template template = configuration.getTemplate("test");
    assertEquals("""
              a b
              c d
            """,
        template.process(Map.of("sequence", List.of(new Complex("a", "b"), new Complex("c", "d")))));
  }
}