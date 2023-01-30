package org.freshmarker.core.ftl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ftl.ParseException;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import org.freshmarker.Configuration;
import org.freshmarker.Template;
import org.freshmarker.core.StringTemplateLoader;
import org.freshmarker.core.environment.TemplateObjectSupplier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TemplateObjectSupplierTest {

  private Configuration configuration;
  private StringTemplateLoader templateLoader;

  @BeforeEach
  public void setUp() {
    configuration = new Configuration();
    configuration.setLocale(Locale.GERMANY);
    templateLoader = new StringTemplateLoader();
    configuration.registerTemplateLoader(templateLoader);
  }

  @Test
  void test() throws ParseException, IOException {
    templateLoader.putTemplate("test", "test: ${test}");
    Template template = configuration.getTemplate("test");
    assertEquals("test: eins", template.process(Map.of("test", (TemplateObjectSupplier<Object>) () -> "eins")));
    assertEquals("test: eins", template.process(Map.of("test", TemplateObjectSupplier.of(() -> "eins"))));
  }
}