package org.freshmarker.core.ftl;

import ftl.ParseException;
import org.freshmarker.Configuration;
import org.freshmarker.Template;
import org.freshmarker.core.environment.TemplateObjectSupplier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TemplateObjectSupplierTest {

    private Configuration configuration;

    @BeforeEach
    void setUp() {
        configuration = new Configuration();
        configuration.setLocale(Locale.GERMANY);
    }

    @Test
    void test() throws ParseException {
        Template template = configuration.getTemplate("test", "test: ${test}");
        assertEquals("test: eins", template.process(Map.of("test", (TemplateObjectSupplier<Object>) () -> "eins")));
        assertEquals("test: eins", template.process(Map.of("test", TemplateObjectSupplier.of(() -> "eins"))));
    }
}