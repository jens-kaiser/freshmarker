package org.freshmarker.core.ftl;

import ftl.ParseException;
import org.freshmarker.Configuration.TemplateBuilder;
import org.freshmarker.Template;
import org.freshmarker.core.SwitchDirectiveFeature;
import org.freshmarker.test.util.TemplateBuilderParameterResolver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Map;

import static org.freshmarker.core.SwitchDirectiveFeature.ALLOW_ONLY_CONSTANT_CASES;
import static org.freshmarker.core.SwitchDirectiveFeature.ALLOW_ONLY_CONSTANT_ONS;
import static org.freshmarker.core.SwitchDirectiveFeature.ALLOW_ONLY_EQUAL_TYPE_CASES;
import static org.freshmarker.core.SwitchDirectiveFeature.ALLOW_ONLY_EQUAL_TYPE_ONS;
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
            "2, test: BBB2",
            "CCC, 'test: '",
    })
    void switchCase(String text, String expected, TemplateBuilder builder) throws ParseException {
        Template template = builder.getTemplate("test",
                "test: <#switch text><#case 'AAA'>${text}1<#case 2>BBB${text}</#switch>");
        assertEquals(expected, template.process(Map.of("text", "2".equals(text) ? 2 : text)));
    }

    @ParameterizedTest
    @CsvSource({
            "AAA, AAA, test: AAA",
            "BBB, AAA, test: BBB",
            "AAA, aaa, test: aaa",
    })
    void switchVariableCase(String text1, String text2, String expected, TemplateBuilder builder) throws ParseException {
        Template template = builder.getTemplate("test",
                "test: <#switch text1><#case text2?upper_case>${text2}<#default>BBB</#switch>");
        assertEquals(expected, template.process(Map.of("text1", text1, "text2", text2)));
    }

    @Test
    void switchVariableCaseForbidden(TemplateBuilder builder) throws ParseException {
        TemplateBuilder builder2 = builder.with(SwitchDirectiveFeature.ALLOW_ONLY_CONSTANT_CASES);
        assertThrows(ParsingException.class, () -> builder2.getTemplate("test",
                "test: <#switch text1><#case text2?upper_case>${text2}</#switch>"));
    }

    @Test
    void switchCaseWithForbiddenDifferentType(TemplateBuilder builder) throws ParseException {
        TemplateBuilder modifiedBuilder = builder.with(ALLOW_ONLY_EQUAL_TYPE_CASES).with(ALLOW_ONLY_CONSTANT_CASES);
        assertThrows(ParsingException.class, () -> modifiedBuilder.getTemplate("test",
                "test: <#switch text><#case 'AAA'>${text?upper_case}1<#case 2>${text?upper_case}2</#switch>"));
    }

    @Test
    void switchVariableOnForbidden(TemplateBuilder builder) throws ParseException {
        TemplateBuilder modifiedBuilder = builder.with(ALLOW_ONLY_CONSTANT_ONS);
        assertThrows(ParsingException.class, () -> modifiedBuilder.getTemplate("test",
                "test: <#switch text1><#on text2?upper_case>${text2}</#switch>"));
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

    @ParameterizedTest
    @CsvSource({
            "AAA, test: AAA1",
            "BBB, test: BBB2",
            "CCC, 'test: '",
    })
    void switchOn(String text, String expected, TemplateBuilder builder) throws ParseException {
        Template template = builder.getTemplate("test",
                "test: <#switch text><#on 'AAA'>${text}1<#on 'BBB'>${text}2</#switch>");
        assertEquals(expected, template.process(Map.of("text", text)));
    }

    @ParameterizedTest
    @CsvSource({
            "AAA, test: AAA1",
            "BBB, test: BBB2",
            "aaa, test: AAA1",
            "bbb, test: BBB2",
            "CCC, 'test: '",
    })
    void switchOnMultiple(String text, String expected, TemplateBuilder builder) throws ParseException {
        Template template = builder.getTemplate("test",
                "test: <#switch text><#on 'AAA', 'aaa'>${text?upper_case}1<#on 'BBB', 'bbb'>${text?upper_case}2</#switch>");
        assertEquals(expected, template.process(Map.of("text", text)));
    }

    @ParameterizedTest
    @CsvSource({
            "AAA, test: AAA",
            "1, test: AAA",
            "BBB, test: BBB",
            "aaa, test: AAA",
            "bbb, test: BBB",
            "CCC, 'test: '",
    })
    void switchOnMultipleWithDifferentType(String text, String expected, TemplateBuilder builder) throws ParseException {
        Template template = builder.getTemplate("test",
                "test: <#switch text><#on 'AAA', 'aaa', 1>AAA<#on 'BBB', 'bbb', 2>${text?upper_case}</#switch>");
        assertEquals(expected, template.process(Map.of("text", "1".equals(text) ? 1 : text)));
    }

    @Test
    void switchOnWithForbiddenDifferentType(TemplateBuilder builder) throws ParseException {
        TemplateBuilder modifiedBuilder = builder.with(ALLOW_ONLY_EQUAL_TYPE_ONS).with(ALLOW_ONLY_CONSTANT_ONS);
        assertThrows(ParsingException.class, () -> modifiedBuilder.getTemplate("test",
                "test: <#switch text><#on 'AAA', 1>${text?upper_case}1<#on 'BBB', 2>${text?upper_case}2</#switch>"));
    }

    @Test
    void mixedCaseAndOn(TemplateBuilder builder) throws ParseException {
        ParsingException exception = assertThrows(ParsingException.class, () -> builder.getTemplate("test",
                "test: <#switch text><#case 'AAA'>AAA1<#on 'AAA'>AAA1</#switch>"));
        assertEquals("switch directive contains on and case at test:1:7 '<#switch text><#case 'AAA'>AAA1<#on 'AAA'>AAA1</#switch>'", exception.getMessage());
    }
}