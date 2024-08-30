package org.freshmarker.core;

import org.freshmarker.core.environment.BaseEnvironment;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
class ProcessContextTest {
    @Test
    void getStore(@Mock Environment environment, @Mock BaseEnvironment baseEnvironment) {
        ProcessContext context = new ProcessContext(baseEnvironment, environment, null, null, null, null, null, null);
        context.getStore("store1").put("key", "value");
        assertEquals("value", context.getStore("store1").get("key"));
        assertNull(context.getStore("store2").get("key"));
    }
}