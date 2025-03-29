package org.freshmarker.core.extension;

import ftl.ParseException;
import org.freshmarker.Configuration;
import org.freshmarker.Template;
import org.freshmarker.api.FunctionProvider;
import org.freshmarker.core.model.TemplateNull;
import org.freshmarker.core.model.primitive.TemplateNumber;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FunctionProviderTest {

    private Configuration configuration;

    @BeforeEach
    void setUp() {
        configuration = new Configuration();
        configuration.register((FunctionProvider) () -> Map.of("abs", (context, args) -> args.getFirst() instanceof  TemplateNumber number ? number.abs() : TemplateNull.NULL,
                "avg", ((context, args) -> args.stream().map(o -> o.evaluate(context, TemplateNumber.class))
                        .reduce(TemplateNumber::add).orElseThrow().divide(new TemplateNumber(args.size())))));
    }

    @ParameterizedTest
    @CsvSource(value = {
            "test: ${avg(10, 20)}<#-- -->;test: 15",
            "test: ${avg(10, 20, 30, 40)}<#-- -->;test: 25",
            "test: ${abs(-10)};test: 10",
    }, delimiterString = ";", ignoreLeadingAndTrailingWhitespace = false)
    void avg(String templateSource, String expected) throws ParseException {
        Template template = configuration.builder().getTemplate("test", templateSource);
        assertEquals(expected, template.process(Map.of("test", "test")));
    }
}