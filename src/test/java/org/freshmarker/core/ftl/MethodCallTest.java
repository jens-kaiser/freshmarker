package org.freshmarker.core.ftl;

import ftl.ParseException;
import org.freshmarker.Configuration;
import org.freshmarker.Template;
import org.freshmarker.core.model.TemplateNull;
import org.freshmarker.core.model.primitive.TemplateNumber;
import org.freshmarker.core.model.primitive.TemplateString;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MethodCallTest {

    private Configuration configuration;

    @BeforeEach
    void setUp() {
        configuration = new Configuration();
        configuration.registerFunction("abs",
                (context, args) -> args.getFirst() instanceof  TemplateNumber number ? number.abs() : TemplateNull.NULL);
        configuration.registerFunction("avg",
                (context, args) -> args.stream().map(o -> o.evaluate(context, TemplateNumber.class))
                        .reduce(TemplateNumber::add).orElseThrow().divide(new TemplateNumber(args.size())));
        configuration.registerFunction("nl", (context, args) -> TemplateString.EMPTY);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "test: ${avg(10, 20)}<#-- -->;test: 15",
            "test: ${avg(10, 20, 30, 40)}<#-- -->;test: 25",
            "test: ${abs(-10)};test: 10",
            "test: ${nl()};test: ",
    }, delimiterString = ";", ignoreLeadingAndTrailingWhitespace = false)
    void avg(String templateSource, String expected) throws ParseException {
        Template template = configuration.builder().getTemplate("test", templateSource);
        assertEquals(expected, template.process(Map.of("test", "test")));
    }
}