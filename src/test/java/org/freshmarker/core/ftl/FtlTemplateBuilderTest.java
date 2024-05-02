package org.freshmarker.core.ftl;

import ftl.ParseException;
import org.freshmarker.Configuration;
import org.freshmarker.Template;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FtlTemplateBuilderTest {

    private Configuration configuration;

    @BeforeEach
    public void setUp() {
        configuration = new Configuration();
        configuration.setLocale(Locale.GERMANY);
    }

    @Test
    void generateWithMap() throws IOException, ParseException {
        Template template = configuration.getTemplate("test", "${bean.name}");
        assertEquals("Bean Name", template.process(Map.of("bean", Map.of("name", "Bean Name"))));
    }

    @Test
    void generateOnlytext() throws ParseException {
        Template template = configuration.getTemplate("test", "the lazy dog jumps over the quick brown fox");
        assertEquals("the lazy dog jumps over the quick brown fox", template.process(Map.of()));
    }

    @Test
    void generateTextInterpolation() throws ParseException {
        Template template = configuration.getTemplate("test", "test: ${'test'}");
        assertEquals("test: test", template.process(Map.of()));
    }

    @Test
    void generateHtmlInterpolation() throws ParseException {
        configuration.setOutputFormat("HTML");
        Template template = configuration.getTemplate("test", "test: ${'<br/>'}");
        assertEquals("test: &lt;br/&gt;", template.process(Map.of()));
    }

    @Test
    void generateHtmlInterpolationEsc() throws ParseException {
        Template template = configuration.getTemplate("test", "test: ${'<br/>'?esc('HTML')}");
        assertEquals("test: &lt;br/&gt;", template.process(Map.of()));
    }

    @Test
    void generateHtmlInterpolationNoEsc() throws ParseException {
        configuration.setOutputFormat("HTML");
        Template template = configuration.getTemplate("test", "test: ${'<br/>'?no_esc}");
        assertEquals("test: <br/>", template.process(Map.of()));
    }

    @Test
    void generateDirectives() throws ParseException {
        Template template = configuration.getTemplate("test",
                "test: <#list 1..4 as s><#if s % 2 == 0>${s} is even<#else>${s} is odd</#if> </#list>");
        assertEquals("test: 1 is odd 2 is even 3 is odd 4 is even ", template.process(Map.of()));
    }

    @Test
    void generateMultiListDirectives() throws ParseException {
        Template template = configuration.getTemplate("test", """
                <#list list as s with l>\
                <#list 1..2 as s with l>\
                <#if s % 2 == 0>${s} is even<#else>${s} is odd</#if>\
                <#if l?has_next>, </#if>\
                </#list>\
                <#if l?is_last>.<#else>, </#if>\
                </#list>""");
        Map<String, Object> dataModel = Map.of("list", List.of(1, 2, 3, 4));
        assertEquals("1 is odd, 2 is even, 1 is odd, 2 is even, 1 is odd, 2 is even, 1 is odd, 2 is even.", template.process(
                dataModel));
    }

    @Test
    void generateSequenceInterpolation() throws ParseException {
        Template template = configuration.getTemplate("test", "test: ${test[1]}");
        assertEquals("test: 2", template.process(Map.of("test", List.of(1, 2, 3, 4, 5))));
    }
}