package org.freshmarker.core.ftl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ftl.ParseException;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.freshmarker.Configuration;
import org.freshmarker.Template;
import org.freshmarker.core.StringTemplateLoader;
import org.freshmarker.core.directive.LoggingDirective;
import org.freshmarker.core.directive.OneLinerDirective;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class VariableTest {
  private Configuration configuration;
  private StringTemplateLoader templateLoader;

  @BeforeEach
  public void setUp() {
    configuration = new Configuration();
    configuration.setLocale(Locale.GERMANY);
    templateLoader = new StringTemplateLoader();
    configuration.registerTemplateLoader(templateLoader);
  }

  @ParameterizedTest
  @CsvSource({
      "test: <#var test='eins'/>${test}, test: eins",
      "test: <#var test='eins'/><#set test='zwei'/>${test}, test: zwei",
      "test: <#var test='eins'/><#set test='zwei'/><#set test='drei'/>${test}, test: drei",
  })
  void setVariable(String templateSource, String expected) throws ParseException, IOException {
    templateLoader.putTemplate("test", templateSource);
    configuration.registerUserDirective( "log", new LoggingDirective());
    Template template = configuration.getTemplate("test");
    assertEquals(expected, template.process(Map.of()));
  }
}