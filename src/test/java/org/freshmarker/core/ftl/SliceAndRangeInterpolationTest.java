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

class SliceAndRangeInterpolationTest {
  private Configuration configuration;
  private StringTemplateLoader templateLoader;

  @BeforeEach
  public void setUp() {
    configuration = new Configuration();
    templateLoader = new StringTemplateLoader();
    configuration.registerTemplateLoader(templateLoader);
  }

  @Test
  void interpolationSliceInclusive() throws ParseException, IOException {
    templateLoader.putTemplate("test", "test: ${list[2..4][1]}");
    Template template = configuration.getTemplate("test");
    assertEquals("test: 4", template.process(Map.of("list", List.of(1,2,3,4,5,6,7))));
  }

  @Test
  void interpolationSliceRightUnbound() throws ParseException, IOException {
    templateLoader.putTemplate("test", "test: ${list[2..][1]}");
    Template template = configuration.getTemplate("test");
    template.process(Map.of("list", List.of(1,2,3,4,5,6,7)));
    assertEquals("test: 4", template.process(Map.of("list", List.of(1,2,3,4,5,6,7))));
  }

  @Test
  void interpolationRangeRightUnbound() throws ParseException, IOException {
    templateLoader.putTemplate("test", "test: ${(0..)[100]}");
    Template template = configuration.getTemplate("test");
    assertEquals("test: 100", template.process(Map.of()));
  }

  @Test
  void interpolationRange() throws ParseException, IOException {
    templateLoader.putTemplate("test", "test: ${(0..20)[10]}");
    Template template = configuration.getTemplate("test");
    assertEquals("test: 10", template.process(Map.of()));
  }
}