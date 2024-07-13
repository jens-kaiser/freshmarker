package org.freshmarker.core.ftl;

import ftl.ParseException;
import org.freshmarker.Configuration;
import org.freshmarker.Template;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BuiltInVariableTestIT {
    private Configuration configuration;

    @BeforeEach
    void setUp() {
        configuration = new Configuration();
        configuration.setLocale(Locale.GERMANY);
    }

    @Test
    void builtInVariables() throws ParseException {
        Template template = configuration.getTemplate("test", "test: ${.version}");
        assertEquals("test: 1.0.3-SNAPSHOT", template.process(Map.of()));
    }
}