package org.freshmarker.core.extension;

import org.freshmarker.Configuration;
import org.freshmarker.Template;
import org.freshmarker.api.OutputFormat;
import org.freshmarker.api.extension.OutputFormatProvider;
import org.freshmarker.core.model.primitive.TemplateString;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.owasp.html.Sanitizers;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OutputFormatProviderTest {
    @ParameterizedTest
    @CsvSource({
            "<h1>This is a header</h1> <p>this is a paragraph</p>,&lt;h1&gt;This is a header&lt;/h1&gt; &lt;p&gt;this is a paragraph&lt;/p&gt;#This is a header this is a paragraph",
            "This is <i>italic</i> and <b>bold</b> text,This is &lt;i&gt;italic&lt;/i&gt; and &lt;b&gt;bold&lt;/b&gt; text#This is <i>italic</i> and <b>bold</b> text",
    })
    void owasp(String content, String expected) {
        Configuration configuration = new Configuration();
        configuration.register((OutputFormatProvider) () -> Map.of("OWASP", new OutputFormat() {
            @Override
            public TemplateString escape(TemplateString value) {
                return new TemplateString(Sanitizers.FORMATTING.sanitize(value.getValue()));
            }
        }));
        Template template = configuration.builder().withOutputFormat("HTML").getTemplate("test", "${content}#<#outputformat 'OWASP'>${content}</#outputformat>");
        assertEquals(expected, template.process(Map.of("content", content)));
    }
}
