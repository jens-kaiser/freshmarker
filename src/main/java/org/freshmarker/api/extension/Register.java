package org.freshmarker.api.extension;

import java.util.Map;

public interface Register<T, N, V> {
    void add(T type, N name, V value);

    Iterable<T> types();

    Map<N, V> byType(T type);
}
