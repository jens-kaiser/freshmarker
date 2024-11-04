package org.freshmarker.core.ftl;

import ftl.ParseException;
import org.freshmarker.Configuration;
import org.freshmarker.Configuration.TemplateBuilder;
import org.freshmarker.Template;
import org.freshmarker.core.ProcessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class IfDirectiveTest {

    private TemplateBuilder builder;

    @BeforeEach
    void setUp() {
        Configuration configuration = new Configuration();
        builder = configuration.builder();
    }

    @ParameterizedTest
    @CsvSource({
            "AAA, test: AAA1",
            "BBB, test: BBB2",
            "CCC, test: CCC3",
    })
    void ifElseifElse(String text, String expected) throws ParseException {
        Template template = builder.getTemplate("test",
                "test: <#if text?contains('A')>${text}1<#elseif text?contains('BB')>${text}2<#else>${text}3</#if>");
        assertEquals(expected, template.process(Map.of("text", text)));
    }

    @ParameterizedTest
    @CsvSource({
            "AAA, test: AAA1",
            "BBB, test: BBB2",
            "CCC, 'test: '",
    })
    void ifElseif(String text, String expected) throws ParseException {
        Template template = builder.getTemplate("test",
                "test: <#if text?contains('A')>${text}1<#elseif text?contains('BB')>${text}2</#if>");
        assertEquals(expected, template.process(Map.of("text", text)));
    }

    @ParameterizedTest
    @CsvSource({
            "AAA, test: AAA1",
            "BBB, test: BBB3",
            "CCC, test: CCC3",
    })
    void ifElse(String text, String expected) throws ParseException {
        Template template = builder.getTemplate("test",
                "test: <#if text?contains('A')>${text}1<#else>${text}3</#if>");
        assertEquals(expected, template.process(Map.of("text", text)));
    }

    @ParameterizedTest
    @CsvSource({
            "AAA",
            "BBB",
            "CCC",
    })
    void emptyIfElseifElse(String text) throws ParseException {
        Template template = builder.getTemplate("test",
                "<#if text?contains('A')><#elseif text?contains('BB')><#else></#if>");
        assertEquals("", template.process(Map.of("text", text)));
    }

    @Test
    void variableScope() throws ParseException {
        Template template = builder.getTemplate("test",
                "<#if text?contains('A')><#var name='Gonzo'/>${name}</#if> ${name!'Kermit'}");
        assertEquals("Gonzo Kermit", template.process(Map.of("text", "A")));
    }

    @ParameterizedTest
    @CsvSource(value = {
            "<#if text != null><#var name='Gonzo'/>${name}</#if> ${name!'Kermit'},Gonzo Kermit",
            "<#if text == null><#var name='Gonzo'/>${name}</#if> ${name!'Kermit'}, Kermit",
            "<#if null != text><#var name='Gonzo'/>${name}</#if> ${name!'Kermit'},Gonzo Kermit",
            "<#if null == text><#var name='Gonzo'/>${name}</#if> ${name!'Kermit'}, Kermit",
            "<#if null != null><#var name='Gonzo'/>${name}</#if> ${name!'Kermit'}, Kermit",
            "<#if null == null><#var name='Gonzo'/>${name}</#if> ${name!'Kermit'},Gonzo Kermit"
    }, ignoreLeadingAndTrailingWhitespace = false)
    void nullCompare(String input, String expected) throws ParseException {
        Template template = builder.getTemplate("test", input);
        assertEquals(expected, template.process(Map.of("text", "A")));
    }

    @ParameterizedTest
    @CsvSource({
            "<#if text1 != text2><#var name='Gonzo'/>${name}</#if> ${name!'Kermit'}",
            "<#if text2 != text1><#var name='Gonzo'/>${name}</#if> ${name!'Kermit'}"
    })
    void invalidNullCompare(String input) throws ParseException {
        Template template = builder.getTemplate("test", input);
        Map<String, Object> model = Map.of();
        assertThrows(ProcessException.class, () -> template.process(model));
    }
}