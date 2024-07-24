package org.freshmarker.core.environment;

import org.freshmarker.core.model.primitive.TemplateString;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BaseEnvironmentTest {
    private BaseEnvironment environment;

    @BeforeEach
    void setUp() {
        environment = new BaseEnvironment(Map.of(), List.of(), Map.of(), Map.of(), null, null);
    }

    @Test
    void getNestedContent() {
        assertEquals(Optional.empty(), environment.getNestedContent());
    }

    @Test
    void checkVariable() {
        assertFalse(environment.checkVariable("name"));
    }

    @Test
    void createVariable() {
        assertThrows(UnsupportedOperationException.class, () -> environment.createVariable("name", new TemplateString("value")));
    }

    @Test
    void setVariable() {
        assertThrows(UnsupportedOperationException.class, () -> environment.setVariable("name", new TemplateString("value")));
    }
}