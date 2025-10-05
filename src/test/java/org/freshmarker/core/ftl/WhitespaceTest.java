package org.freshmarker.core.ftl;

import ftl.ParseException;
import org.freshmarker.Template;
import org.freshmarker.TemplateBuilder;
import org.freshmarker.test.util.TemplateBuilderParameterResolver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(TemplateBuilderParameterResolver.class)
class WhitespaceTest {
    @Test
    void text(TemplateBuilder templateBuilder) throws ParseException {
        Template template = templateBuilder.getTemplate("c", """
                xxx
                xxx
                """);
        assertEquals("xxx\nxxx\n", template.process(Map.of()));
    }

    @Test
    void directive(TemplateBuilder templateBuilder) throws ParseException {
        Template template = templateBuilder.getTemplate("c", """
                <#if true>
                 xxx
                  </#if>
                xxx
                """);
        assertEquals(" xxx\nxxx\n", template.process(Map.of()));
    }

    @Test
    void directiveAtEOF(TemplateBuilder templateBuilder) throws ParseException {
        Template template = templateBuilder.getTemplate("c", """
                <#if true>\s\s
                xxx
                 </#if>""");
        assertEquals("xxx\n", template.process(Map.of()));
    }

    @Test
    void directiveInOneLine(TemplateBuilder templateBuilder) throws ParseException {
        Template template = templateBuilder.getTemplate("c", """
                <#if true>xxx</#if>
                """);
        assertEquals("xxx\n", template.process(Map.of()));
    }

    @Test
    void directiveInOneLineWithoutWhitespaceAtTheEnd(TemplateBuilder templateBuilder) throws ParseException {
        Template template = templateBuilder.getTemplate("c", "<#if true> xxx</#if>");
        assertEquals(" xxx", template.process(Map.of()));
    }

    @Test
    void directiveInOneLine2(TemplateBuilder templateBuilder) throws ParseException {
        Template template = templateBuilder.getTemplate("c", """
                <#if true>
                xxx
                </#if>
                """);
        assertEquals("xxx\n", template.process(Map.of()));
    }

    @Test
    void directiveInOneLine3(TemplateBuilder templateBuilder) throws ParseException {
        Template template = templateBuilder.getTemplate("c", """
                <#if true>
                xxx</#if>
                xxx""");
        assertEquals("xxx\nxxx", template.process(Map.of()));
    }

    @Test
    void directiveWithOneliner(TemplateBuilder templateBuilder) throws ParseException {
        Template template = templateBuilder.getTemplate("c", """
                <#if true><@oneliner>
                 xxx
                </@oneliner></#if>
                """);
        assertEquals(" xxx ", template.process(Map.of()));
    }

    @Test
    void directiveWithOnelinerAndCompress(TemplateBuilder templateBuilder) throws ParseException {
        Template template = templateBuilder.getTemplate("c", """
                <#if true> <@compress><@oneliner>
                 xxx
                </@oneliner></@compress></#if>
                xxx
                """);
        assertEquals(" xxxxxx\n", template.process(Map.of()));
    }
}
