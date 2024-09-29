package org.freshmarker.core.ftl;

import ftl.ParseException;
import org.freshmarker.Configuration;
import org.freshmarker.Template;
import org.freshmarker.core.Environment;
import org.freshmarker.core.model.primitive.TemplateString;
import org.freshmarker.core.output.OutputFormat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.owasp.html.Sanitizers;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OutputFormatTest {
    private Configuration configuration;

    @BeforeEach
    void setUp() {
        configuration = new Configuration();
    }

    @ParameterizedTest
    @CsvSource({
            "HTML,<>\"',test: &lt;&gt;&quot;&#39;",
            "XHTML,<>\"',test: &lt;&gt;&quot;&#39;",
            "XML,<>\"',test: &lt;&gt;&quot;&apos;",
            "undefined,<>\"',test: <>\"'",
            "plainText,<>\"',test: <>\"'",
            "JavaScript,<>\"',test: <>\"'",
            "JSON,<>\"',test: <>\"'",
            "CSS,<>\"',test: <>\"'",
    })
    void interpolation(String format, String content, String expected) throws ParseException {
        Template template = configuration.builder().withOutputFormat(format).getTemplate("test", "test: ${content}");
        assertEquals(expected, template.process(Map.of("content", content)));
    }

    @Test
    void htmlOutputFormatBlock() throws ParseException {
        Template template = configuration.builder().getTemplate("test", "test: ${content}<#outputformat 'HTML'>${content}</#outputformat>${content}");
        assertEquals("test: <>\"'&lt;&gt;&quot;&#39;<>\"'", template.process(Map.of("content", "<>\"'")));
    }

    @Test
    void noEscHtmlOutputFormatBlock() throws ParseException {
        Template template = configuration.builder().getTemplate("test", "test: ${content}<#outputformat 'HTML'>${content?noEsc}</#outputformat>${content}");
        assertEquals("test: <>\"'<>\"'<>\"'", template.process(Map.of("content", "<>\"'")));
    }

    @ParameterizedTest
    @CsvSource({
            "HTML,<>\"',test: <>\"'",
            "XHTML,<>\"',test: <>\"'",
            "XML,<>\"',test: <>\"'",
            "undefined,<>\"',test: <>\"'",
            "plainText,<>\"',test: <>\"'",
            "JavaScript,<>\"',test: <>\"'",
            "JSON,<>\"',test: <>\"'",
            "CSS,<>\"',test: <>\"'",
    })
    void unescapeInterpolation(String format, String content, String expected) throws ParseException {
        Template template = configuration.builder().withOutputFormat(format).getTemplate("test", "test: ${content?noEsc}");
        assertEquals(expected, template.process(Map.of("content", content)));
    }

    @ParameterizedTest
    @CsvSource(value = {
            "value1,value 2#value1#value 2",
            "\"value,value\",value 2#value,value#value 2",
            "value1,\"value\"\"2\"#value1#value\"2",
            "value1,\"value@2\"#value1#value@2",
    }, delimiterString = "#")
    void csvOutputFormatBlock(String expected, String value1, String value2) throws ParseException {
        configuration.registerOutputFormat("CSV", new OutputFormat() {
            @Override
            public TemplateString escape(Environment environment, String value) {
                boolean escaped = value.contains("\"");
                if (escaped) {
                    value = value.replaceAll("\"", "\"\"");
                }
                if (escaped || value.contains("\n") || value.contains(",")) {
                    value = '"' + value + '"';
                }
                return new TemplateString(value);
            }
        });
        Template template = configuration.builder().getTemplate("test", """
                VALUE1,VALUE2
                <#outputformat 'CSV'>
                <#list sequence as s>
                ${s.value1},${s.value2}
                </#list>
                </#outputformat>""");
        Map<String, String> row = Map.of("value1", value1, "value2", value2.replace('@', '\n'));
        assertEquals("VALUE1,VALUE2\n" + expected.replace('@', '\n') + "\n", template.process(Map.of("sequence", List.of(row))));
    }

    @ParameterizedTest
    @CsvSource({
        "<h1>This is a header</h1> <p>this is a paragraph</p>,This is a header this is a paragraph",
        "This is <i>italic</i> and <b>bold</b> text,This is <i>italic</i> and <b>bold</b> text",
    })
    void interpolationWithCustomOutputFormat(String content, String expected) throws ParseException {
        Template template = configuration.builder().withOutputFormat(new OutputFormat() {

            @Override
            public TemplateString escape(Environment environment, String value) {
                return new TemplateString(Sanitizers.FORMATTING.sanitize(value));
            }
        }).getTemplate("test", "${content}");
        assertEquals(expected, template.process(Map.of("content", content)));
    }

    @ParameterizedTest
    @CsvSource({
            "<h1>This is a header</h1> <p>this is a paragraph</p>,&lt;h1&gt;This is a header&lt;/h1&gt; &lt;p&gt;this is a paragraph&lt;/p&gt;#This is a header this is a paragraph",
            "This is <i>italic</i> and <b>bold</b> text,This is &lt;i&gt;italic&lt;/i&gt; and &lt;b&gt;bold&lt;/b&gt; text#This is <i>italic</i> and <b>bold</b> text",
    })
    void interpolationWithRegisteredCustomOutputFormat(String content, String expected) throws ParseException {
        configuration.registerOutputFormat("OWASP", new OutputFormat() {

            @Override
            public TemplateString escape(Environment environment, String value) {
                return new TemplateString(Sanitizers.FORMATTING.sanitize(value));
            }
        });
        Template template = configuration.builder().withOutputFormat("HTML").getTemplate("test", "${content}#<#outputformat 'OWASP'>${content}</#outputformat>");
        assertEquals(expected, template.process(Map.of("content", content)));
    }
}
