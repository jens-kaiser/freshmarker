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

class MacroTest {

    private TemplateBuilder builder;

    @BeforeEach
    void setUp() {
        builder = new Configuration().builder();
    }

    @ParameterizedTest
    @CsvSource(value = {
            "<#macro empty></#macro><@empty/>;''",
            "<#macro empty><!-- --></#macro><@empty/>;<!-- -->",
            "<#macro comment><!-- <#nested/> --></#macro><@comment>Dies ist ein Kommentar</@comment>;<!-- Dies ist ein Kommentar -->",
            "<#macro comment><#nested/> <#nested/></#macro><@comment>Hurra</@comment>;Hurra Hurra",
            "<#macro entry label value>${label}=${value}</#macro><@entry label=text value='value'/>;tralala=value",
            "<#macro entry label, value >${label}=${value}</#macro><@entry label=text value='value'/>;tralala=value",
            "<#macro entry(label, value)>${label}=${value}</#macro><@entry label=text value='value'/>;tralala=value",
            "<#macro entry(label value)>${label}=${value}</#macro><@entry label=text value='value'/>;tralala=value",
            "<#macro entry(label value)>${label}=${value}</#macro><@entry label=text, value='value'/>;tralala=value",
            "<#macro test>ABC<#return/>DEF</#macro><@test/>;ABC",
    }, delimiterString = ";")
    void generateMacro(String templateSource, String expected) throws ParseException {
        Template template = builder.getTemplate("test", templateSource);
        assertEquals(expected, template.process(Map.of("bean", Map.of(), "text", "tralala")));
    }

    @Test
    void generateFunction() throws ParseException {
        assertThrows(ParseException.class, () -> builder.getTemplate("test", "<#function entry count></#function"));
    }

    @Test
    void generateInvalidMacros() throws ParseException {
        assertThrows(ParseException.class, () -> builder.getTemplate("test", "<#macro entry(label value>${label}=${value}</#macro><@entry label=text value='value'/>;tralala=value"));
    }

    @Test
    void generateUnknownMacros() throws ParseException {
        Template template = builder.getTemplate("test", "<@gonzo />");
        assertThrows(ProcessException.class, () -> template.process(Map.of("bean", Map.of(), "text", "tralala")));
    }

    @Test
    void generateComplexMacro() throws ParseException {
        Template template = builder.getTemplate("test", "<#macro entry count><#list 1..count as v>${v} <#nested/>\n</#list></#macro><@entry count=3>test</@entry>");
        assertEquals("1 test\n2 test\n3 test\n", template.process(Map.of()));
    }

    @Test
    void generateMacroWithDefaultValue() throws ParseException {
        Template template = builder.getTemplate("test", "<#macro entry count=4><#list 1..count as v>${v} <#nested/>\n</#list></#macro><@entry>test</@entry>");
        assertEquals("1 test\n2 test\n3 test\n4 test\n", template.process(Map.of()));
    }

    @Test
    void generateMacroWithInvalidParameters() throws ParseException {
        ParsingException exception = assertThrows(ParsingException.class, () -> builder.getTemplate("test", "<#macro entry label label>${label}=${value}</#macro><@entry label='label' value='value'/>"));
        assertEquals("non unique parameter name at test:1:21 'label'", exception.getMessage());
    }

    @Test
    void macroVariableContext() throws ParseException {
        Template template = builder.getTemplate("test", "<#macro copyright><#var test='test'/></#macro><@copyright/>${test!'gonzo'}");
        assertEquals("gonzo", template.process(Map.of()));
    }
}
