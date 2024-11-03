package org.freshmarker.core.providers;

import org.freshmarker.core.ProcessException;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.beans.IntrospectionException;
import java.beans.Introspector;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BeanMethodProviderTest {

    @Test
    void applyWithException() {
        try (MockedStatic<Introspector> mockedStatic = Mockito.mockStatic(Introspector.class)) {
            mockedStatic.when(() -> Introspector.getBeanInfo(BeanMethodProviderTest.class, Object.class))
                    .thenThrow(new IntrospectionException(""));
            BeanMethodProvider provider = new BeanMethodProvider();
            ProcessException exception = assertThrows(ProcessException.class, () -> provider.apply(BeanMethodProviderTest.class));
            assertEquals("", exception.getMessage());
        }
    }
}