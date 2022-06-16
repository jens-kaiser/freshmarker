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

class BeanInterpolationTest {

  private Configuration configuration;
  private StringTemplateLoader templateLoader;

  public static class TestBean {

    private final String name;
    private final boolean active;

    TestBean(String name, boolean active) {
      this.name = name;
      this.active = active;
    }

    public String getName() {
      return name;
    }

    public boolean isActive() {
      return active;
    }
  }

  @BeforeEach
  public void setUp() {
    configuration = new Configuration();
    templateLoader = new StringTemplateLoader();
    configuration.registerTemplateLoader(templateLoader);
  }

  @Test
  void generateWithBean() throws IOException, ParseException {
    templateLoader.putTemplate("test", "${bean.name} ${bean.active}");
    Template template = configuration.getTemplate("test");
    assertEquals("Bean Name yes", template.process(Map.of("bean", new TestBean("Bean Name", true))));
  }

  @Test
  void generateWithUnknownBeanAttribute() throws IOException, ParseException {
    templateLoader.putTemplate("test", "${bean.value} ${bean.active}");
    Template template = configuration.getTemplate("test");
    Map<String, Object> data = Map.of("bean", new TestBean("Bean Name", true));
    assertThrows(ProcessException.class, () -> template.process(data));
  }
}