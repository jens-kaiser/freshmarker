package org.freshmarker.core.ftl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ftl.ParseException;
import java.io.IOException;
import java.util.Map;
import org.freshmarker.Configuration;
import org.freshmarker.Template;
import org.freshmarker.core.StringTemplateLoader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MacroTest {

  private Configuration configuration;
  private StringTemplateLoader templateLoader;

  @BeforeEach
  public void setUp() {
    configuration = new Configuration();
    templateLoader = new StringTemplateLoader();
    configuration.registerTemplateLoader(templateLoader);
  }

  @Test
  void generateEmptyMacro() throws IOException, ParseException {
    templateLoader.putTemplate("test", "<#macro empty></#macro><@empty/>");
    Template template = configuration.getTemplate("test");
    assertEquals("", template.process(Map.of("bean", Map.of())));
  }

  @Test
  void generateMacroWithoutParameters() throws IOException, ParseException {
    templateLoader.putTemplate("test", "<#macro empty><!-- --></#macro><@empty/>");
    Template template = configuration.getTemplate("test");
    assertEquals("<!-- -->", template.process(Map.of()));
  }

  @Test
  void generateMacroWithNestedContent() throws IOException, ParseException {
    templateLoader.putTemplate("test",
        "<#macro comment><!-- <#nested/> --></#macro><@comment>Dies ist ein Kommentar</@comment>");
    Template template = configuration.getTemplate("test");
    assertEquals("<!-- Dies ist ein Kommentar -->", template.process(Map.of()));
  }

  @Test
  void generateMacroWithDoubleNestedContent() throws IOException, ParseException {
    templateLoader.putTemplate("test", "<#macro comment><#nested/> <#nested/></#macro><@comment>Hurra</@comment>");
    Template template = configuration.getTemplate("test");
    assertEquals("Hurra Hurra", template.process(Map.of()));
  }

  @Test
  void generateMacroWithParameters() throws IOException, ParseException {
    templateLoader.putTemplate("test",
        "<#macro entry label value>${label}=${value}</#macro><@entry label='label' value='value'/>");
    Template template = configuration.getTemplate("test");
    assertEquals("label=value", template.process(Map.of()));
  }

  @Test
  void generateComplexMacro() throws IOException, ParseException {
    templateLoader.putTemplate("test",
        "<#macro entry count><#list 1..count as v>${v} <#nested/>\n</#list></#macro><@entry count=3>test</@entry>");
    Template template = configuration.getTemplate("test");
    assertEquals("1 test\n2 test\n3 test\n", template.process(Map.of()));
  }
}