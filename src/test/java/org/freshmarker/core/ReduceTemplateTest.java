package org.freshmarker.core;

import org.freshmarker.Configuration;
import org.freshmarker.TemplateBuilder;
import org.freshmarker.ReductionStatus;
import org.freshmarker.Template;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ReduceTemplateTest {
    private ReductionStatus reductionStatus;
    private TemplateBuilder templateBuilder;

    @BeforeEach
    void setUp() {
        templateBuilder = new Configuration().builder();
        reductionStatus = new ReductionStatus();
    }

    @Test
    void reduceBlock() {
        Map<String, Object> model = Map.of("company", "schegge.de");
        Template template = templateBuilder.getTemplate("test", "${company}: ${name}").reduce(model, reductionStatus);
        assertNotNull(template);
        assertEquals("schegge.de: Jens Kaiser", template.process(Map.of("name", "Jens Kaiser")));
        assertEquals(4, reductionStatus.total().get());
        assertEquals(0, reductionStatus.deleted().get());
        assertEquals(1, reductionStatus.changed().get());
    }

    @Test
    void reduceBlockWithDefault() {
        Map<String, Object> model = Map.of("company", "schegge.de");
        Template template = templateBuilder.getTemplate("test", "${company} ${name!}").reduce(model, reductionStatus);
        assertNotNull(template);
        assertEquals("schegge.de Jens Kaiser", template.process(Map.of("name", "Jens Kaiser")));
        assertEquals(0, reductionStatus.deleted().get());
    }

    @Test
    void reduceOutputFormat() {
        Map<String, Object> model = Map.of("company", "schegge.de");
        Template template = templateBuilder.getTemplate("test", "<#outputformat 'HTML'>${company}: ${name}</#outputformat>").reduce(model, reductionStatus);
        assertNotNull(template);
        assertEquals("schegge.de: Jens Kaiser", template.process(Map.of("name", "Jens Kaiser")));
        assertEquals(6, reductionStatus.total().get());
        assertEquals(0, reductionStatus.deleted().get());
        assertEquals(1, reductionStatus.changed().get());
    }

    @Test
    void reduceIf() {
        Map<String, Object> model = Map.of("company", "schegge.de", "flag", true);
        Template template = templateBuilder.getTemplate("test", "<#if flag>${company}<#else>${name}</#if>").reduce(model, reductionStatus);
        assertNotNull(template);
        assertEquals("schegge.de", template.process(Map.of("name", "Jens Kaiser", "flag", true)));
        assertEquals(5, reductionStatus.total().get());
        assertEquals(3, reductionStatus.deleted().get());
    }

    @Test
    void reduceNotIf() {
        Map<String, Object> model = Map.of("company", "schegge.de");
        Template template = templateBuilder.getTemplate("test", "<#if flag>${company}<#else>${name}</#if>").reduce(model, reductionStatus);
        assertNotNull(template);
        assertEquals("schegge.de", template.process(Map.of("name", "Jens Kaiser", "flag", true)));
        assertEquals(5, reductionStatus.total().get());
        assertEquals(0, reductionStatus.deleted().get());
    }

    @Test
    void reduceIfWithExists() {
        Map<String, Object> model = Map.of("company", "schegge.de");
        Template template = templateBuilder.getTemplate("test", "<#if name??>${company}<#else>${name}</#if>").reduce(model, reductionStatus);
        assertNotNull(template);
        assertEquals("schegge.de", template.process(Map.of("name", "Jens Kaiser")));
        assertEquals(0, reductionStatus.deleted().get());
        assertEquals(2, reductionStatus.changed().get());
    }

    @Test
    void reduceMultipleIf() {
        Map<String, Object> model = Map.of("company", "schegge.de", "flag", 3);
        Template template = templateBuilder.getTemplate("test", """
                <#if flag == 1>
                ${company}1
                <#elseif flag == 2>
                ${company}2
                <#elseif flag == 3>
                ${company}3
                <#else>
                ${name}
                </#if>
                """).reduce(model, reductionStatus);
        assertNotNull(template);
        assertEquals("schegge.de3\n", template.process(Map.of("name", "Jens Kaiser", "flag", true)));
        assertEquals(17, reductionStatus.total().get());
        assertEquals(14, reductionStatus.deleted().get());

    }

    @Test
    void reduceIfElseWithException() {
        Map<String, Object> model = Map.of("company", "schegge.de", "flag", 3);
        Template template = templateBuilder.getTemplate("test", """
                <#if flag == 1>
                ${company} 1
                <#elseif galf == 2>
                ${company} 2
                </#if>
                """).reduce(model, reductionStatus);
        assertEquals(0, reductionStatus.deleted().get());
        assertEquals(4, reductionStatus.changed().get());
        assertNotNull(template);
        assertEquals("schegge.de 2\n", template.process(Map.of("flag", 3, "galf", 2)));
    }

    @Test
    void reduceElse() {
        Map<String, Object> model = Map.of("company", "schegge.de", "flag", false);
        Template template = templateBuilder.getTemplate("test", "<#if flag>${company}<#else>${name}</#if>").reduce(model, reductionStatus);
        assertNotNull(template);
        assertEquals("Jens Kaiser", template.process(Map.of("name", "Jens Kaiser", "flag", true)));
        assertEquals(3, reductionStatus.deleted().get());
    }

    @ParameterizedTest
    @CsvSource({
            "<#switch flag><#case 1>${company}<#case 2>${name}<#case 3>three<#default>default</#switch>,1,schegge.de",
            "<#switch flag><#case 1>${company}<#case 2>${name}<#case 3>three<#default>default</#switch>,2,Jens Kaiser",
            "<#switch flag><#case 1>${company}<#case 2>${name}<#case 3>three<#default>default</#switch>,3,three",
    })
    void reduceSwitchCase(String input, int flag, String expected) {
        Map<String, Object> reduceModel = Map.of("company", "schegge.de", "flag", flag);
        Template template = templateBuilder.getTemplate("test", input);
        Template reducedTemplate = template.reduce(reduceModel, reductionStatus);
        assertNotNull(reducedTemplate);
        assertEquals(expected, reducedTemplate.process(Map.of("name", "Jens Kaiser", "flag", 2)));
        assertEquals(6, reductionStatus.deleted().get());
    }

    @Test
    void reduceNotSwitch() {
        Map<String, Object> reduceModel = Map.of("company", "schegge.de");
        String input = "<#switch flag><#case 1>${company}<#case 2>${name}<#case 3>three<#default>default</#switch>";
        Template template = templateBuilder.getTemplate("test", input);
        Template reducedTemplate = template.reduce(reduceModel, reductionStatus);
        assertNotNull(reducedTemplate);
        assertEquals("Jens Kaiser", reducedTemplate.process(Map.of("name", "Jens Kaiser", "flag", 2)));
        assertEquals(0, reductionStatus.deleted().get());
        assertEquals(2, reductionStatus.changed().get());
    }

    @Test
    void reduceSwitchDefault() {
        Map<String, Object> reduceModel = Map.of("company", "schegge.de", "flag", 4);
        String input = "<#switch flag><#case 1>${company}<#case 2>${name}<#case 3>three<#default>default</#switch>";
        Template template = templateBuilder.getTemplate("test", input);
        Template reducedTemplate = template.reduce(reduceModel, reductionStatus);
        assertNotNull(reducedTemplate);
        assertEquals("default", reducedTemplate.process(Map.of("name", "Jens Kaiser", "flag", 2)));
        assertEquals(7, reductionStatus.deleted().get());
    }

    @Test
    void reduceSwitchWithoutCaseReduction() {
        Map<String, Object> reduceModel = Map.of("company", "schegge.de");
        String input = "<#switch flag><#case 1>${company}<#case 2>${name}<#case 3>three<#default>default</#switch>";
        Template template = templateBuilder.getTemplate("test", input);
        Template reducedTemplate = template.reduce(reduceModel, reductionStatus);
        assertNotNull(reducedTemplate);
        assertEquals("Jens Kaiser", reducedTemplate.process(Map.of("name", "Jens Kaiser", "flag", 2)));
        assertEquals(0, reductionStatus.deleted().get());
        assertEquals(2, reductionStatus.changed().get());
    }

    @Test
    void reduceList() {
        Map<String, Object> reduceModel = Map.of("company", "schegge.de", "seq", List.of(1, 2, 3, 4));
        String input = "<#list seq as s>${company} </#list>";
        Template template = templateBuilder.getTemplate("test", input);
        Template reducedTemplate = template.reduce(reduceModel, reductionStatus);
        assertNotNull(reducedTemplate);
        assertEquals(5, reductionStatus.total().get());
        assertEquals(0, reductionStatus.deleted().get());
        assertEquals(1, reductionStatus.changed().get());
        assertEquals("schegge.de schegge.de schegge.de schegge.de ", reducedTemplate.process(Map.of("seq", List.of(1, 2, 3, 4))));
    }

    @Test
    void reduceNotListList() {
        Map<String, Object> reduceModel = Map.of("company", "schegge.de");
        String input = "<#list seq as s>${company} </#list>";
        Template template = templateBuilder.getTemplate("test", input);
        Template reducedTemplate = template.reduce(reduceModel, reductionStatus);
        assertNotNull(reducedTemplate);
        assertEquals(5, reductionStatus.total().get());
        assertEquals(0, reductionStatus.deleted().get());
        assertEquals(1, reductionStatus.changed().get());
        assertEquals("schegge.de schegge.de schegge.de schegge.de ", reducedTemplate.process(Map.of("seq", List.of(1, 2, 3, 4))));
    }

    @Test
    void reduceListWithHiddenValue() {
        Map<String, Object> reduceModel = Map.of("company", "schegge.de", "seq", List.of(1, 2, 3, 4));
        String input = "<#list seq as company>${company} </#list>";
        Template template = templateBuilder.getTemplate("test", input);
        Template reducedTemplate = template.reduce(reduceModel, reductionStatus);
        assertNotNull(reducedTemplate);
        assertEquals(5, reductionStatus.total().get());
        assertEquals(0, reductionStatus.deleted().get());
        assertEquals(0, reductionStatus.changed().get());
        assertEquals("1 2 3 4 ", reducedTemplate.process(Map.of("seq", List.of(1, 2, 3, 4))));
    }

    @Test
    void reduceListWithItem() {
        Map<String, Object> reduceModel = Map.of("company", "schegge.de");
        String input = "<#list seq as s>${company}/${s} </#list>";
        Template template = templateBuilder.getTemplate("test", input);
        Template reducedTemplate = template.reduce(reduceModel, reductionStatus);
        assertNotNull(reducedTemplate);
        assertEquals(7, reductionStatus.total().get());
        assertEquals(0, reductionStatus.deleted().get());
        assertEquals(1, reductionStatus.changed().get());
        assertEquals("schegge.de/1 schegge.de/2 schegge.de/3 schegge.de/4 ", reducedTemplate.process(Map.of("seq", List.of(1, 2, 3, 4))));
    }

    @Test
    void reduceListWithLoopVariable() {
        Map<String, Object> reduceModel = Map.of("company", "schegge.de");
        String input = "<#list seq as s with l>${l?counter} ${company}/${s} </#list>";
        Template template = templateBuilder.getTemplate("test", input);
        Template reducedTemplate = template.reduce(reduceModel, reductionStatus);
        assertNotNull(reducedTemplate);
        assertEquals(9, reductionStatus.total().get());
        assertEquals(0, reductionStatus.deleted().get());
        assertEquals(1, reductionStatus.changed().get());
        assertEquals("1 schegge.de/1 2 schegge.de/2 3 schegge.de/3 4 schegge.de/4 ", reducedTemplate.process(Map.of("seq", List.of(1, 2, 3, 4))));
    }

    @Test
    void reduceListWithLoopVariableAndVVariable() {
        Map<String, Object> reduceModel = Map.of("company", "schegge.de", "firstname", "jens");
        String input = """
                <#var name=firstname>
                <#list seq as s with l>
                ${l?counter}. ${company}/${s} ${name}
                </#list>""";
        Template template = templateBuilder.getTemplate("test", input);
        Template reducedTemplate = template.reduce(reduceModel, reductionStatus);
        assertNotNull(reducedTemplate);
        assertEquals(13, reductionStatus.total().get());
        assertEquals(1, reductionStatus.deleted().get());
        assertEquals(2, reductionStatus.changed().get());
        assertEquals("""
                1. schegge.de/1 jens
                2. schegge.de/2 jens
                3. schegge.de/3 jens
                4. schegge.de/4 jens
                """, reducedTemplate.process(Map.of("seq", List.of(1, 2, 3, 4))));
    }

    @Test
    void reduceStackedListWithLoopVariable() {
        Map<String, Object> reduceModel = Map.of("company", "schegge.de", "firstname", "jens");
        String input = """
                <#var name=0>
                <#list seq1 as s with l>
                <#list seq2 as s with l>
                <#set name=name+1>
                ${l?counter}. ${company}/${s} ${name}
                </#list>
                </#list>""";
        Template template = templateBuilder.getTemplate("test", input);
        Template reducedTemplate = template.reduce(reduceModel, reductionStatus);
        assertNotNull(reducedTemplate);
        assertEquals(15, reductionStatus.total().get());
        assertEquals(1, reductionStatus.deleted().get());
        assertEquals(2, reductionStatus.changed().get());
        assertEquals("""
                1. schegge.de/3 1
                2. schegge.de/4 2
                1. schegge.de/3 3
                2. schegge.de/4 4
                """, reducedTemplate.process(Map.of("seq1", List.of(1, 2), "seq2", List.of(3, 4))));
    }

    @Test
    void reduceHashList() {
        Map<String, Object> reduceModel = Map.of("company", "schegge.de", "seq", Map.of(1, 2, 2, 4));
        String input = "<#list seq as k, v>${company} </#list>";
        Template template = templateBuilder.getTemplate("test", input);
        Template reducedTemplate = template.reduce(reduceModel, reductionStatus);
        assertNotNull(reducedTemplate);
        assertEquals(5, reductionStatus.total().get());
        assertEquals(0, reductionStatus.deleted().get());
        assertEquals(1, reductionStatus.changed().get());
        assertEquals("schegge.de schegge.de ", reducedTemplate.process(Map.of("seq", Map.of(1, 2, 2, 4))));
    }

    @Test
    void reduceWithoutStatus() {
        Map<String, Object> reduceModel = Map.of("company", "schegge.de");
        String input = "<#list seq as s>${company}/${s} </#list>";
        Template template = templateBuilder.getTemplate("test", input);
        assertNotNull(template.reduce(reduceModel));
    }

    @Test
    void reduceWithConstantVariable() {
        Map<String, Object> reduceModel = Map.of("company", "schegge.de");
        String input = "<#var name='Jens'><#list seq as s>${company}/${s} ${name?upper_case}</#list>";
        Template template = templateBuilder.getTemplate("test", input);
        Template reducedTemplate = template.reduce(reduceModel, reductionStatus);
        assertNotNull(reducedTemplate);
        assertEquals(10, reductionStatus.total().get());
        assertEquals(1, reductionStatus.deleted().get());
        assertEquals(2, reductionStatus.changed().get());
        assertEquals("schegge.de/1 JENSschegge.de/2 JENSschegge.de/3 JENSschegge.de/4 JENS", reducedTemplate.process(Map.of("seq", List.of(1, 2, 3, 4))));
    }

    @Test
    void reduceWithDynamicVariable() {
        Map<String, Object> reduceModel = Map.of("company", "schegge.de", "firstname", "jens");
        String input = "<#var name=firstname><#list seq as s>${company}/${s} ${name?upper_case}</#list>";
        Template template = templateBuilder.getTemplate("test", input);
        Template reducedTemplate = template.reduce(reduceModel, reductionStatus);
        assertNotNull(reducedTemplate);
        assertEquals(10, reductionStatus.total().get());
        assertEquals(1, reductionStatus.deleted().get());
        assertEquals(2, reductionStatus.changed().get());
        assertEquals("schegge.de/1 JENSschegge.de/2 JENSschegge.de/3 JENSschegge.de/4 JENS", reducedTemplate.process(Map.of("seq", List.of(1, 2, 3, 4))));
    }

    @Test
    void reduceWithDynamicVariableSet() {
        Map<String, Object> reduceModel = Map.of("company", "schegge.de", "firstname", "jens");
        String input = "<#var name='Jens'><#set name=firstname><#list seq as s>${company}/${s} ${name?upper_case}</#list>";
        Template template = templateBuilder.getTemplate("test", input);
        Template reducedTemplate = template.reduce(reduceModel, reductionStatus);
        assertNotNull(reducedTemplate);
        assertEquals(11, reductionStatus.total().get());
        assertEquals(1, reductionStatus.deleted().get());
        assertEquals(3, reductionStatus.changed().get());
        assertEquals("schegge.de/1 JENSschegge.de/2 JENSschegge.de/3 JENSschegge.de/4 JENS", reducedTemplate.process(Map.of("seq", List.of(1, 2, 3, 4))));
    }

    @Test
    void reduceWithEmptyVariable() {
        Map<String, Object> reduceModel = Map.of("company", "schegge.de");
        String input = "<#var name=firstname><#list seq as s>${company}/${s} ${name?upper_case}</#list>";
        Template template = templateBuilder.getTemplate("test", input);
        Template reducedTemplate = template.reduce(reduceModel, reductionStatus);
        assertNotNull(reducedTemplate);
        assertEquals(10, reductionStatus.total().get());
        assertEquals(1, reductionStatus.deleted().get());
        assertEquals(1, reductionStatus.changed().get());
        assertEquals("schegge.de/1 JENSschegge.de/2 JENSschegge.de/3 JENSschegge.de/4 JENS",
                reducedTemplate.process(Map.of("seq", List.of(1, 2, 3, 4), "firstname", "jens")));
    }

    @Test
    void reduceWithWrongType() {
        Map<String, Object> reduceModel = Map.of("company", "schegge.de");
        Template template = templateBuilder.getTemplate("test", "${1 + company}");
        assertThrows(ReduceException.class, () -> template.reduce(reduceModel, reductionStatus));
    }

    @Test
    void demo() {
        String input = """
                <#switch flag>
                <#case 1>${company}
                <#case 2>${name!'Jens'}
                <#case 3><#if name??>${name}<#else>Anonymous</#if>
                <#default>default
                </#switch>""";
        Template template = templateBuilder.getTemplate("test", input);
        Template reducedTemplate = template.reduce(Map.of("flag", 3), reductionStatus);
        assertNotNull(reducedTemplate);
        assertEquals(18, reductionStatus.total().get());
        assertEquals(10, reductionStatus.deleted().get());
        assertEquals(1, reductionStatus.changed().get());
        assertEquals("Jens Kaiser\n", reducedTemplate.process(Map.of("name", "Jens Kaiser", "flag", 3)));
    }
}
