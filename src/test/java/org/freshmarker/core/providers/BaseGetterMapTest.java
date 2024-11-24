package org.freshmarker.core.providers;

import org.freshmarker.core.ModelSecurityGateway;
import org.freshmarker.core.model.TemplateBean;
import org.freshmarker.core.model.TemplateNull;
import org.freshmarker.core.model.primitive.TemplateString;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
class BaseGetterMapTest {

    private record Example(String test) {
    }

    private TemplateMapGetterProvider provider;

    @BeforeEach
    void setUp() {
        provider = new TemplateMapGetterProvider( new RecordMethodProvider());
    }

    @Test
    void getKnownAttribute(@Mock TemplateObjectMapper mapper) {
        Mockito.when(mapper.mapObject("test")).thenReturn(new TemplateString("test"));
        Map<String, Object> map = provider.provide(new Example("test"), mapper);
        Assertions.assertEquals(new TemplateString("test"), map.get("test"));
    }

    @Test
    void getUnknownAttribute(@Mock TemplateObjectMapper mapper) {
        Map<String, Object> map = provider.provide(new Example("test"), mapper);
        Assertions.assertNull(map.get("tset"));
    }

    @Test
    void unsupportedIteratorRemoveOperations(@Mock TemplateObjectMapper mapper) {
        Map<String, Object> map = provider.provide(new Example("test"), mapper);
        Iterator<Entry<String, Object>> iterator = map.entrySet().iterator();
        Assertions.assertThrows(UnsupportedOperationException.class, iterator::remove);
    }

    @Test
    void unsupportedIteratorSetValueOperations(@Mock TemplateObjectMapper mapper) {
        Map<String, Object> map = provider.provide(new Example("test"), mapper);
        Entry<String, Object> entry = map.entrySet().iterator().next();
        Assertions.assertThrows(UnsupportedOperationException.class, () -> entry.setValue("test"));
    }
}