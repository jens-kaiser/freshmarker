package org.freshmarker.core.ftl;

import ftl.ParseException;
import org.freshmarker.Configuration;
import org.freshmarker.Template;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class IfDirectiveTest {

    private Configuration configuration;

    @BeforeEach
    public void setUp() {
        configuration = new Configuration();
    }

    @ParameterizedTest
    @CsvSource({
            "AAA, test: AAA1",
            "BBB, test: BBB2",
            "CCC, test: CCC3",
    })
    void ifElseifElse(String text, String expected) throws ParseException {
        Template template = configuration.getTemplate("test",
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
        Template template = configuration.getTemplate("test",
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
        Template template = configuration.getTemplate("test",
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
        Template template = configuration.getTemplate("test",
                "<#if text?contains('A')><#elseif text?contains('BB')><#else></#if>");
        assertEquals("", template.process(Map.of("text", text)));
    }

    @Test
    void variableScope() throws ParseException {
        Template template = configuration.getTemplate("test",
                "<#if text?contains('A')><#var name='Gonzo'/>${name}</#if> ${name!'Kermit'}");
        assertEquals("Gonzo Kermit", template.process(Map.of("text", "A")));
    }
}