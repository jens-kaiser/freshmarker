package org.freshmarker.core;

import org.freshmarker.core.environment.BaseEnvironment;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
class ProcessContextTest {
    @Test
    void getStore(@Mock StaticContext staticContext, @Mock BaseEnvironment baseEnvironment) {
        LocalContext localContext = new LocalContext(Locale.GERMANY, null, null, null, null);
        ProcessContext context = new ProcessContext(staticContext, baseEnvironment, Map.of(), null, null, null, localContext);
        context.getStore("store1").put("key", "value");
        assertEquals("value", context.getStore("store1").get("key"));
        assertNull(context.getStore("store2").get("key"));
    }
}