package org.freshmarker.core.ftl;

import ftl.ParseException;
import org.freshmarker.Configuration;
import org.freshmarker.Template;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OutputFormatTest {
    private Configuration configuration;

    @BeforeEach
    public void setUp() {
        configuration = new Configuration();
        configuration.setLocale(Locale.GERMANY);
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
        configuration.setOutputFormat(format);
        Template template = configuration.getTemplate("test", "test: ${content}");
        assertEquals(expected, template.process(Map.of("content", content)));
    }

    @Test
    void htmlOutputFormatBlock() throws ParseException {
        Template template = configuration.getTemplate("test", "test: ${content}<#outputformat 'HTML'>${content}</#outputformat>${content}");
        assertEquals("test: <>\"'&lt;&gt;&quot;&#39;<>\"'", template.process(Map.of("content", "<>\"'")));
    }

    @Test
    void noEscHtmlOutputFormatBlock() throws ParseException {
        Template template = configuration.getTemplate("test", "test: ${content}<#outputformat 'HTML'>${content?noEsc}</#outputformat>${content}");
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
        configuration.setOutputFormat(format);
        Template template = configuration.getTemplate("test", "test: ${content?noEsc}");
        assertEquals(expected, template.process(Map.of("content", content)));
    }
}
