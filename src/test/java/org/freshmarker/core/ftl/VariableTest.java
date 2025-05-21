package org.freshmarker.core.ftl;

import ftl.ParseException;
import org.freshmarker.TemplateBuilder;
import org.freshmarker.Template;
import org.freshmarker.core.ProcessException;
import org.freshmarker.test.util.TemplateBuilderParameterResolver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(TemplateBuilderParameterResolver.class)
class VariableTest {

    @ParameterizedTest
    @CsvSource({
            "test: <#var test/>${test!1}, test: 1",
            "test: <#var test/><#set test='eins'/>${test}, test: eins",
            "test: <#var test='eins'/>${test}, test: eins",
            "test: <#var test='eins'/><#set test='zwei'/>${test}, test: zwei",
            "test: <#var test='eins'/><#set test='zwei'/><#set test='drei'/>${test}, test: drei",
            "test: <#var test/><#assign test='eins'/>${test}, test: eins",
            "test: <#var test='eins'/><#assign test='zwei'/>${test}, test: zwei",
            "test: <#var test='eins'/><#assign test='zwei'/><#assign test='drei'/>${test}, test: drei",
    })
    void setVariable(String templateSource, String expected, TemplateBuilder builder) throws ParseException {
        Template template = builder.getTemplate("test", templateSource);
        assertEquals(expected, template.process(Map.of()));
    }

    @ParameterizedTest
    @CsvSource(value = {
            "test: <#var test1, test2/>${test1!1} ${test2!2}; test: 1 2",
            "test: <#var test1 test2/><#set test1='eins' test2='zwei'/>${test1} ${test2}; test: eins zwei",
            "test: <#var test1, test2/><#set test1='eins', test2='zwei'/>${test1} ${test2}; test: eins zwei",
            "test: <#var test1='eins' test2='zwei'/>${test1} ${test2}; test: eins zwei",
            "test: <#var test1='eins', test2='zwei'/>${test1} ${test2}; test: eins zwei",
            "test: <#var test1 test2/><#assign test1='eins' test2='zwei'/>${test1} ${test2}; test: eins zwei",
            "test: <#var test1, test2/><#assign test1='eins', test2='zwei'/>${test1} ${test2}; test: eins zwei",
    }, delimiterString = ";")
    void setVariables(String templateSource, String expected, TemplateBuilder builder) throws ParseException {
        Template template = builder.getTemplate("test", templateSource);
        assertEquals(expected, template.process(Map.of()));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "test: <#set test='zwei'/>",
            "test: <#var test='eins'/><#var test='eins'/>",
    })
    void invalid(String templateSource, TemplateBuilder builder) throws ParseException {
        Template template = builder.getTemplate("test", templateSource);
        Map<String, Object> dataModel = Map.of();
        assertThrows(ProcessException.class, () -> template.process(dataModel));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "test: <#var test1 test1/>",
            "test: <#var test1, test1/>",
            "test: <#var test1='eins' test1='eins'/>",
            "test: <#var test1='eins', test1='eins'/>",
            "test: <#set test1='eins' test1='eins'/>",
            "test: <#set test1='eins', test1='eins'/>",
    })
    void notUnique(String templateSource, TemplateBuilder builder) throws ParseException {
        assertThrows(ParsingException.class, () -> builder.getTemplate("test", templateSource));
    }

    @Test
    void nested(TemplateBuilder builder) {
        Template template = builder.getTemplate("test", """
                <#var v="test">
                ${v}
                <#list sequence as s>
                  <#var v=s>
                ${v}
                </#list>
                ${v}
                """);
        assertEquals("test\n1\n2\n3\ntest\n", template.process(Map.of("sequence", List.of(1, 2, 3))));
    }

    @Test
    void counter(TemplateBuilder builder) {
        Template template = builder.getTemplate("test", """
                <#var v=0>
                ${v}
                <#list sequence as s>
                  <#set v=v+1>
                ${v}
                </#list>
                ${v}
                """);
        assertEquals("0\n1\n2\n3\n3\n", template.process(Map.of("sequence", List.of(1, 2, 3))));
    }
}