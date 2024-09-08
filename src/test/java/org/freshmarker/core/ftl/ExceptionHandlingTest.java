package org.freshmarker.core.ftl;

import ftl.ParseException;
import org.freshmarker.Configuration;
import org.freshmarker.Configuration.TemplateBuilder;
import org.freshmarker.Template;
import org.freshmarker.core.UnsupportedBuiltInException;
import org.freshmarker.core.WrongTypeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ExceptionHandlingTest {

    private TemplateBuilder templateBuilder;

    @BeforeEach
    void setUp() {
        templateBuilder = new Configuration().builder().withLocale(Locale.GERMANY);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "test: ${true?}",
            "test: ${false?string('ja','nein')",
    })
    void parseError(String templateSource) {
        assertThrows(ParseException.class, () -> templateBuilder.getTemplate("test", templateSource));
    }

    @Test
    void builtInTypeError() throws ParseException {
        Template template = templateBuilder.getTemplate("test", "test: ${true?upper_case}");
        Map<String, Object> dataModel = Map.of();
        UnsupportedBuiltInException exception = assertThrows(UnsupportedBuiltInException.class,
                () -> template.process(dataModel));
        assertEquals("unsupported builtin 'upper_case' for TemplateBoolean at test:1:7 '${true?upper_case}'",
                exception.getMessage());
    }

    @Test
    void wrongTypeError() throws ParseException {
        Template template = templateBuilder.getTemplate("test", "test: ${!test}");
        Map<String, Object> dataModel = Map.of("test", 42);
        WrongTypeException exception = assertThrows(WrongTypeException.class, () -> template.process(dataModel));
        assertEquals("expected TemplateBoolean but is TemplateNumber (42) at test:1:7 '${!test}'",
                exception.getMessage());
    }

    @Test
    void ifConditionError() throws ParseException {
        Template template = templateBuilder.getTemplate("test",
                """
                        test:\s
                        <#if text?contains('A')>
                        ${text}1
                        <#elseif text?contains('BB')>
                        ${text}2
                        <#else>
                        ${text}3
                        </#if>""");
        Map<String, Object> dataModel = Map.of("text", 42);
        UnsupportedBuiltInException exception = assertThrows(UnsupportedBuiltInException.class,
                () -> template.process(dataModel));
        assertEquals("unsupported builtin 'contains' for TemplateNumber at test:2:6 'text?contains('A')'", exception.getMessage());
    }

    @Test
    void elseIfConditionError() throws ParseException {
        Template template = templateBuilder.getTemplate("test",
                """
                        test:\s
                        <#if text1?contains('A')>
                        ${text1}1
                        <#elseif text2?contains('B')>
                        ${text2}2
                        <#else>
                        ${text}3
                        </#if>""");
        Map<String, Object> dataModel = Map.of("text1", "B", "text2", 42);
        UnsupportedBuiltInException exception = assertThrows(UnsupportedBuiltInException.class, () -> template.process(dataModel));
        assertEquals("unsupported builtin 'contains' for TemplateNumber at test:4:10 'text2?contains('B')'", exception.getMessage());
    }

    @Test
    void ifBlockError() throws ParseException {
        Template template = templateBuilder.getTemplate("test",
                """
                        test:
                        <#if text?contains('A')>
                        ${text?xxx}1
                        <#elseif text?contains('BB')>
                        ${text}2
                        <#else>${text}3
                        </#if>""");
        Map<String, Object> dataModel = Map.of("text", "A");
        UnsupportedBuiltInException exception = assertThrows(UnsupportedBuiltInException.class,
                () -> template.process(dataModel));
        assertEquals("unsupported builtin 'xxx' for TemplateString at test:3:1 '${text?xxx}'", exception.getMessage());
    }

    @Test
    void switchExpressionError() throws ParseException {
        Template template = templateBuilder.getTemplate("test",
                "test:\n<#switch text?upper_case>\n<#case 'AAA'>${text}1\n<#case 'BBB'>${text}2\n</#switch>");
        Map<String, Object> dataModel = Map.of("text", 42);
        UnsupportedBuiltInException exception = assertThrows(UnsupportedBuiltInException.class,
                () -> template.process(dataModel));
        assertEquals("unsupported builtin 'upper_case' for TemplateNumber at test:2:10 'text?upper_case'", exception.getMessage());
    }
}
