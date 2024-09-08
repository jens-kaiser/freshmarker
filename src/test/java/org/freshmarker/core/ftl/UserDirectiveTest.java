package org.freshmarker.core.ftl;

import ftl.ParseException;
import org.freshmarker.Configuration;
import org.freshmarker.Template;
import org.freshmarker.core.directive.LoggingDirective;
import org.freshmarker.core.directive.OneLinerDirective;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserDirectiveTest {
    private Configuration configuration;

    @BeforeEach
    void setUp() {
        configuration = new Configuration();
    }

    @ParameterizedTest
    @CsvSource({
            "test: <@log level='info' message='test'/>, test: <!-- test -->",
            "test: <@log level='warn' message='test'/>, test: <!-- TEST -->",
    })
    void logDirectiveXML(String templateSource, String expected) throws ParseException {
        configuration.registerUserDirective("log", new LoggingDirective());
        Template template = configuration.builder().withOutputFormat("XML").getTemplate("test", templateSource);
        assertEquals(expected, template.process(Map.of()));
    }

    @ParameterizedTest
    @CsvSource({
            "test: <@log level='info' message='test'/>, test: #////#test#////#",
            "test: <@log level='warn' message='test'/>, test: #////#TEST#////#",
    })
    void logDirectiveADOC(String templateSource, String expected) throws ParseException {
        configuration.registerUserDirective("log", new LoggingDirective());
        Template template = configuration.builder().withOutputFormat("ADOC").getTemplate("test", templateSource);
        assertEquals(expected.replace('#', '\n'), template.process(Map.of()));
    }

    @ParameterizedTest
    @CsvSource(value = {
            "test <@oneliner>1; 2; 3;</@oneliner>:test 1;  2;  3; ",
            "test <@oneliner><#list values as v with l>${v}<#if l?has_next>;</#if></#list></@oneliner>:test 1; 2; 3",
    }, ignoreLeadingAndTrailingWhitespace = false, delimiterString = ":")
    void oneLiner(String templateSource, String expected) throws ParseException {
        configuration.registerUserDirective("oneliner", new OneLinerDirective());
        Template template = configuration.builder().getTemplate("test", templateSource.replace(";", ";\n"));
        assertEquals(expected, template.process(Map.of("values", List.of(1, 2, 3))));
    }
}