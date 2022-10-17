package org.freshmarker.core.ftl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ftl.ParseException;
import java.io.IOException;
import java.util.Map;
import org.freshmarker.Configuration;
import org.freshmarker.Template;
import org.freshmarker.core.ProcessException;
import org.freshmarker.core.StringTemplateLoader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RecordInterpolationTest {

  private Configuration configuration;
  private StringTemplateLoader templateLoader;

  public record TestRecord(String name, boolean active) {
  }

  @BeforeEach
  public void setUp() {
    configuration = new Configuration();
    templateLoader = new StringTemplateLoader();
    configuration.registerTemplateLoader(templateLoader);
  }

  @Test
  void generateWithBean() throws IOException, ParseException {
    templateLoader.putTemplate("test", "${record.name} ${record.active}");
    Template template = configuration.getTemplate("test");
    assertEquals("Record Name yes", template.process(Map.of("record", new TestRecord("Record Name", true))));
  }

  @Test
  void generateWithUnknownBeanAttribute() throws IOException, ParseException {
    templateLoader.putTemplate("test", "${record.value} ${record.active}");
    Template template = configuration.getTemplate("test");
    Map<String, Object> data = Map.of("record", new TestRecord("Record Name", true));
    assertThrows(ProcessException.class, () -> template.process(data));
  }
}