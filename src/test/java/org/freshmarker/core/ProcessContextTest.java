package org.freshmarker.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProcessContextTest {

    @Test
    void getStore() {
        ProcessContext context = new ProcessContext(null, null, null);
        context.getStore("store1").put("key", "value");
        assertEquals("value", context.getStore("store1").get("key"));
        assertNull(context.getStore("store2").get("key"));
    }
}