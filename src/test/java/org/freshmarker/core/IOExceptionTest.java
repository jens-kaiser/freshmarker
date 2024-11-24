package org.freshmarker.core;

import org.freshmarker.Configuration;
import org.freshmarker.Template;
import org.junit.jupiter.api.BeforeEach;
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
class IOExceptionTest {
    private Configuration configuration;

    @BeforeEach
    void setUp() {
        configuration = new Configuration();
    }

    @Test
    void constantFragment(@Mock Writer writer) throws IOException {
        Template template = configuration.builder().getTemplate("test", "test");
        Map<String, Object> model = Map.of();
        doThrow(new IOException("why not")).when(writer).write(anyString());
        assertThrows(ProcessException.class, () -> template.process(model, writer));
    }
}
