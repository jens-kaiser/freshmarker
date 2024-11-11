package org.freshmarker.core.ftl;

import ftl.ParseException;
import org.freshmarker.Configuration.TemplateBuilder;
import org.freshmarker.Template;
import org.freshmarker.test.util.TemplateBuilderParameterResolver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(TemplateBuilderParameterResolver.class)
class SwitchDirectiveTest {

    @ParameterizedTest
    @CsvSource({
            "AAA, test: AAA1",
            "BBB, test: BBB2",
            "CCC, test: CCC3",
    })
    void switchCaseDefault(String text, String expected, TemplateBuilder builder) throws ParseException {
        Template template = builder.getTemplate("test",
                "test: <#switch text><#case 'AAA'>${text}1<#case 'BBB'>${text}2<#default>${text}3</#switch>");
        assertEquals(expected, template.process(Map.of("text", text)));
    }

    @ParameterizedTest
    @CsvSource({
            "AAA, test: AAA1",
            "BBB, test: BBB2",
            "CCC, 'test: '",
    })
    void switchCase(String text, String expected, TemplateBuilder builder) throws ParseException {
        Template template = builder.getTemplate("test",
                "test: <#switch text><#case 'AAA'>${text}1<#case 'BBB'>${text}2</#switch>");
        assertEquals(expected, template.process(Map.of("text", text)));
    }

    @ParameterizedTest
    @CsvSource({
            "AAA, test: AAA1",
            "BBB, test: BBB3",
            "CCC, test: CCC3",
    })
    void switchDefault(String text, String expected, TemplateBuilder builder) throws ParseException {
        Template template = builder.getTemplate("test",
                "test: <#switch text><#case 'AAA'>${text}1<#default>${text}3</#switch>");
        assertEquals(expected, template.process(Map.of("text", text)));
    }

    @Test
    void switchEmptyCase(TemplateBuilder builder) throws ParseException {
        ParsingException exception = assertThrows(ParsingException.class, () -> builder.getTemplate("test",
                "test: <#switch text><#case 'AAA'><#default></#switch>"));
        assertEquals("missing block at test:1:21 '<#case 'AAA'>'", exception.getMessage());
    }

    @Test
    void switchEmptyDefault(TemplateBuilder builder) throws ParseException {
        ParsingException exception = assertThrows(ParsingException.class, () -> builder.getTemplate("test",
                "test: <#switch text><#case 'AAA'>AAA1<#default></#switch>"));
        assertEquals("missing block at test:1:38 '<#default>'", exception.getMessage());
    }
}