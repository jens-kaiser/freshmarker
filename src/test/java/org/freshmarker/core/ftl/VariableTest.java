package org.freshmarker.core.ftl;

import ftl.ParseException;
import org.freshmarker.Configuration;
import org.freshmarker.Template;
import org.freshmarker.core.ProcessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class VariableTest {
    private Configuration configuration;

    @BeforeEach
    void setUp() {
        configuration = new Configuration();
        configuration.setLocale(Locale.GERMANY);
    }

    @ParameterizedTest
    @CsvSource({
            "test: <#var test='eins'/>${test}, test: eins",
            "test: <#var test='eins'/><#set test='zwei'/>${test}, test: zwei",
            "test: <#var test='eins'/><#set test='zwei'/><#set test='drei'/>${test}, test: drei",
    })
    void setVariable(String templateSource, String expected) throws ParseException {
        Template template = configuration.getTemplate("test", templateSource);
        assertEquals(expected, template.process(Map.of()));
    }

    @ParameterizedTest
    @CsvSource({
            "test: <#set test='zwei'/>",
            "test: <#var test='eins'/><#var test='eins'/>",
    })
    void invalid(String templateSource) throws ParseException {
        Template template = configuration.getTemplate("test", templateSource);
        Map<String, Object> dataModel = Map.of();
        assertThrows(UnsupportedOperationException.class, () -> template.process(dataModel));
    }

    @Test
    void unsupported() {
        assertThrows(ParsingException.class, () -> configuration.getTemplate("test", "<#var test1='eins' test2='zwei'/>"));
    }

    @Test
    void nested() {
        Template template = configuration.getTemplate("test", """
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
}