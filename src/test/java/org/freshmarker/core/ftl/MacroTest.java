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
import static org.junit.jupiter.api.Assertions.assertThrows;

class MacroTest {

    private Configuration configuration;

    @BeforeEach
    public void setUp() {
        configuration = new Configuration();
    }

    @ParameterizedTest
    @CsvSource({
            "<#macro empty></#macro><@empty/>,''",
            "<#macro empty><!-- --></#macro><@empty/>,<!-- -->",
            "<#macro comment><!-- <#nested/> --></#macro><@comment>Dies ist ein Kommentar</@comment>,<!-- Dies ist ein Kommentar -->",
            "<#macro comment><#nested/> <#nested/></#macro><@comment>Hurra</@comment>,Hurra Hurra",
            "<#macro entry label value>${label}=${value}</#macro><@entry label='label' value='value'/>,label=value",
            "<#macro test>ABC<#return/>DEF</#macro><@test/>,ABC",
    })
    void generateMacro(String templateSource, String expected) throws ParseException {
        Template template = configuration.getTemplate("test", templateSource);
        assertEquals(expected, template.process(Map.of("bean", Map.of())));
    }

    @Test
    void generateComplexMacro() throws ParseException {
        Template template = configuration.getTemplate("test", "<#macro entry count><#list 1..count as v>${v} <#nested/>\n</#list></#macro><@entry count=3>test</@entry>");
        assertEquals("1 test\n2 test\n3 test\n", template.process(Map.of()));
    }

    @Test
    void generateMacroWithDefaultValue() throws ParseException {
        Template template = configuration.getTemplate("test", "<#macro entry count=4><#list 1..count as v>${v} <#nested/>\n</#list></#macro><@entry>test</@entry>");
        assertEquals("1 test\n2 test\n3 test\n4 test\n", template.process(Map.of()));
    }

    @Test
    void generateMacroWithInvalidParameters() throws ParseException {
        ParsingException exception = assertThrows(ParsingException.class, () -> configuration.getTemplate("test", "<#macro entry label label>${label}=${value}</#macro><@entry label='label' value='value'/>"));
        assertEquals("non unique parameter name at test:1:21 'label'", exception.getMessage());
    }
}
