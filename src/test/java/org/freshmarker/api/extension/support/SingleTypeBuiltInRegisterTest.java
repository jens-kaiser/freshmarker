package org.freshmarker.api.extension.support;

import org.freshmarker.core.model.primitive.TemplateBoolean;
import org.freshmarker.core.model.primitive.TemplateLocale;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SingleTypeBuiltInRegisterTest {
    @Test
    void add() {
        SingleTypeBuiltInRegister register = new SingleTypeBuiltInRegister(TemplateBoolean.class);
        register.add("a", ((value, parameters, context) -> value));
        assertEquals(1, register.asMap().get(TemplateBoolean.class).size());
    }

    @Test
    void addExpectedType() {
        SingleTypeBuiltInRegister register = new SingleTypeBuiltInRegister(TemplateBoolean.class);
        register.add(TemplateBoolean.class, "a", ((value, parameters, context) -> value));
        assertEquals(1, register.asMap().get(TemplateBoolean.class).size());
    }

    @Test
    void addUnexpectedType() {
        SingleTypeBuiltInRegister register = new SingleTypeBuiltInRegister(TemplateBoolean.class);
        register.add(TemplateLocale.class, "a", ((value, parameters, context) -> value));
        assertEquals(0, register.asMap().get(TemplateBoolean.class).size());
    }
}