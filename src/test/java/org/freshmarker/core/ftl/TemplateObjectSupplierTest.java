package org.freshmarker.core.ftl;

import ftl.ParseException;
import org.freshmarker.TemplateBuilder;
import org.freshmarker.Template;
import org.freshmarker.core.environment.TemplateObjectSupplier;
import org.freshmarker.test.util.TemplateBuilderParameterResolver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(TemplateBuilderParameterResolver.class)
class TemplateObjectSupplierTest {

    @Test
    void test(TemplateBuilder builder) throws ParseException {
        Template template = builder.withLocale(Locale.GERMANY).getTemplate("test", "test: ${test}");
        assertEquals("test: eins", template.process(Map.of("test", (TemplateObjectSupplier<Object>) () -> "eins")));
        assertEquals("test: eins", template.process(Map.of("test", TemplateObjectSupplier.of(() -> "eins"))));
    }
}