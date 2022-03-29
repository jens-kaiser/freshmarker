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
  void generateSequenceInterpolation() throws ParseException, IOException {
    templateLoader.putTemplate("test", "test: ${test[1]}");
    Template template = configuration.getTemplate("test");
    assertEquals("test: 2", template.process(Map.of("test", List.of(1, 2, 3, 4, 5))));
  }
}