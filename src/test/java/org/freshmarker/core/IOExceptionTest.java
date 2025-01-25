package org.freshmarker.core;

import org.freshmarker.Configuration;
import org.freshmarker.TemplateBuilder;
import org.freshmarker.Template;
import org.freshmarker.core.directive.LoggingDirective;
import org.freshmarker.test.util.TemplateBuilderParameterResolver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
@ExtendWith(TemplateBuilderParameterResolver.class)
class IOExceptionTest {
    @Test
    void constantFragment(TemplateBuilder builder, @Mock Writer writer) throws IOException {
        Template template = builder.getTemplate("test", "test");
        Map<String, Object> model = Map.of();
        doThrow(new IOException("why not")).when(writer).write(anyString());
        assertThrows(ProcessException.class, () -> template.process(model, writer));
    }

    @Test
    void logDirective(@Mock Writer writer) throws IOException {
        Configuration configuration = new Configuration();
        configuration.registerUserDirective("log", new LoggingDirective());
        Template template = configuration.builder().getTemplate("test", "<@log message='test'/>");
        Map<String, Object> model = Map.of();
        doThrow(new IOException("why not")).when(writer).write(anyString());
        assertThrows(ProcessException.class, () -> template.process(model, writer));
    }
}
