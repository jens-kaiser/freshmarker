package org.freshmarker.core.ftl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ftl.ParseException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.freshmarker.core.StringTemplateLoader;
import org.freshmarker.Configuration;
import org.freshmarker.Template;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FtlTemplateBuilderTest {

  private Configuration configuration;
  private StringTemplateLoader templateLoader;

  @BeforeEach
  public void setUp() {
    configuration = new Configuration();
    templateLoader = new StringTemplateLoader();
    configuration.registerTemplateLoader(templateLoader);
  }

  @Test
  void generateWithMap() throws IOException, ParseException {
    templateLoader.putTemplate("test", "${bean.name}");
    Template template = configuration.getTemplate("test");
    assertEquals("Bean Name", template.process(Map.of("bean", Map.of("name", "Bean Name"))));
  }

  @Test
  void generateOnlytext() throws IOException, ParseException {
    templateLoader.putTemplate("test", "the lazy dog jumps over the quick brown fox");
    Template template = configuration.getTemplate("test");
    assertEquals("the lazy dog jumps over the quick brown fox", template.process(Map.of()));
  }

  @Test
  void generateTextInterpolation() throws ParseException, IOException {
    templateLoader.putTemplate("test", "test: ${'test'}");
    Template template = configuration.getTemplate("test");
    assertEquals("test: test", template.process(Map.of()));
  }

  @Test
  void generateHtmlInterpolation() throws ParseException, IOException {
    templateLoader.putTemplate("test", "test: ${'<br/>'}");
    configuration.setOutputFormat("HTML");
    Template template = configuration.getTemplate("test");
    assertEquals("test: &lt;br/&gt;", template.process(Map.of()));
  }

  @Test
  void generateHtmlInterpolationEsc() throws ParseException, IOException {
    templateLoader.putTemplate("test", "test: ${'<br/>'?esc}");
    configuration.setOutputFormat("HTML");
    Template template = configuration.getTemplate("test");
    assertEquals("test: &lt;br/&gt;", template.process(Map.of()));
  }

  @Test
  void generateHtmlInterpolationNoEsc() throws ParseException, IOException {
    templateLoader.putTemplate("test", "test: ${'<br/>'?no_esc}");
    configuration.setOutputFormat("HTML");
    Template template = configuration.getTemplate("test");
    assertEquals("test: <br/>", template.process(Map.of()));
  }

  @Test
  void generateDirectives() throws ParseException, IOException {
    templateLoader.putTemplate("test", "test: <#list 1..4 as s><#if s % 2 == 0>${s} is even<#else>${s} is odd</#if> </#list>");
    Template template = configuration.getTemplate("test");
    assertEquals("test: 1 is odd 2 is even 3 is odd 4 is even ", template.process(Map.of()));
  }

  @Test
  void generateSequenceInterpolation() throws ParseException, IOException {
    templateLoader.putTemplate("test", "test: ${test[1]}");
    Template template = configuration.getTemplate("test");
    assertEquals("test: 2", template.process(Map.of("test", List.of(1, 2, 3, 4, 5))));
  }
}