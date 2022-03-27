package org.freshmarker.core;

import static org.junit.jupiter.api.Assertions.*;

import ftl.ParseException;
import java.io.IOException;
import java.io.StringWriter;
import java.time.LocalTime;
import java.util.Map;
import org.freshmarker.Configuration;
import org.freshmarker.Template;
import org.junit.jupiter.api.Test;

class ConfigurationTest {

  @Test
  void getTemplate() throws ParseException, IOException {
    Configuration configuration = new Configuration();
    StringTemplateLoader templateLoader = new StringTemplateLoader();
    templateLoader.putTemplate("test", "test: ${temporal}");
    configuration.registerTemplateLoader(templateLoader);
    Template template = configuration.getTemplate("test");
    assertNotNull(template);
    StringWriter writer = new StringWriter();
    template.process(Map.of("temporal", LocalTime.of(12, 30)), writer);
    assertEquals("test: 12:30:00", writer.toString());
  }
}