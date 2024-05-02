package org.freshmarker.core.ftl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ftl.ParseException;
import java.io.IOException;
import java.util.Map;
import org.freshmarker.Configuration;
import org.freshmarker.Template;
import org.freshmarker.core.StringTemplateLoader;
import org.freshmarker.core.model.primitive.TemplateNumber;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class MethodCallTest {

  private Configuration configuration;

  @BeforeEach
  public void setUp() {
    configuration = new Configuration();
    configuration.registerFunction("abs",
        (context, args) -> args.get(0).evaluateToObject(context).asNumber().map(TemplateNumber::abs).orElseThrow());
    configuration.registerFunction("avg",
        (context, args) -> args.stream().map(o -> o.evaluate(context, TemplateNumber.class))
            .reduce(TemplateNumber::add).orElseThrow().divide(new TemplateNumber(args.size())));
  }

  @ParameterizedTest
  @CsvSource(value = {
      "test: ${avg(10, 20)}<#-- -->;test: 15",
      "test: ${avg(10, 20, 30, 40)}<#-- -->;test: 25",
      "test: ${abs(-10)};test: 10",
  }, delimiterString = ";")
  void avg(String templateSource, String expected) throws ParseException {
    Template template = configuration.getTemplate("test", templateSource);
    assertEquals(expected, template.process(Map.of("test", "test")));
  }
}