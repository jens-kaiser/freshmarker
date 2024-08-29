package org.freshmarker.core.providers;

import java.lang.reflect.Method;
import java.lang.reflect.RecordComponent;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

public class RecordMethodProvider implements Function<Class<?>, Map<String, Method>> {
    @Override
    public Map<String, Method> apply(Class<?> type) {
        return Stream.of(type.getRecordComponents()).collect(toMap(RecordComponent::getName, RecordComponent::getAccessor));
    }
}
