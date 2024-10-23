package org.freshmarker.core.buildin;

import org.freshmarker.core.model.primitive.TemplateNumber;
import org.freshmarker.core.model.primitive.TemplateString;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BuiltInKeyTest {

    @Test
    void equality() {
        BuiltInKey key1 = new BuiltInKeyBuilder<>(TemplateString.class).of("test");
        BuiltInKey key2 = new BuiltInKeyBuilder<>(TemplateString.class).of("tset");
        BuiltInKey key3 = new BuiltInKeyBuilder<>(TemplateNumber.class).of("test");
        assertEquals(key1, key1);
        assertEquals(key1.hashCode(), key1.hashCode());
        assertNotEquals(key1, key2);
        assertNotEquals(key1, key3);
        assertFalse(key1.equals(null));
    }
}