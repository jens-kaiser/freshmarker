package org.freshmarker.api.extension;

import java.util.Map;

public interface Register<T, N, V> {
    void add(T type, N name, V value);

    Map<T, Map<N, V>> asMap();
}
