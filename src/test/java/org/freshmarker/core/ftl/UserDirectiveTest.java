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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class UserDirectiveTest {
  private Configuration configuration;
  private StringTemplateLoader templateLoader;

  @BeforeEach
  public void setUp() {
    configuration = new Configuration();
    configuration.setLocale(Locale.GERMANY);
    configuration.setOutputFormat("XML");
    templateLoader = new StringTemplateLoader();
    configuration.registerTemplateLoader(templateLoader);
  }

  @ParameterizedTest
  @CsvSource({
      "test: <@log level='info' message='test'/>, test: <!-- test -->",
      "test: <@log level='warn' message='test'/>, test: <!-- TEST -->",
  })
  void logDirectiveXML(String templateSource, String expected) throws ParseException, IOException {
    templateLoader.putTemplate("test", templateSource);
    configuration.registerUserDirective( "log", new LoggingDirective());
    Template template = configuration.getTemplate("test");
    assertEquals(expected, template.process(Map.of()));
  }

  @ParameterizedTest
  @CsvSource({
      "test: <@log level='info' message='test'/>, test: #////#test#////#",
      "test: <@log level='warn' message='test'/>, test: #////#TEST#////#",
  })
  void logDirectiveADOC(String templateSource, String expected) throws ParseException, IOException {
    templateLoader.putTemplate("test", templateSource);
    configuration.registerUserDirective( "log", new LoggingDirective());
    configuration.setOutputFormat("ADOC");
    Template template = configuration.getTemplate("test");
    assertEquals(expected.replace('#', '\n'), template.process(Map.of()));
  }

  @ParameterizedTest
  @CsvSource(value = {
      "test <@oneliner>1; 2; 3;</@oneliner>:test 1;  2;  3; ",
      "test <@oneliner><#list values as v with l>${v}<#if l?has_next>;</#if></#list></@oneliner>:test 1; 2; 3",
  }, ignoreLeadingAndTrailingWhitespace=false, delimiterString = ":")
  void oneLiner(String templateSource, String expected) throws ParseException, IOException {
    templateLoader.putTemplate("test", templateSource.replace(";", ";\n"));
    configuration.registerUserDirective( "oneliner", new OneLinerDirective());
    Template template = configuration.getTemplate("test");
    assertEquals(expected, template.process(Map.of("values", List.of(1,2,3))));
  }
}