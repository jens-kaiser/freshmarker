package org.freshmarker.core.formatter;

import java.util.Locale;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Function;

public class LocaleLocal<T> {
  private final Function<Locale, ? extends T> creator;

  private final Map<Locale, T> map = new WeakHashMap<>();

  private LocaleLocal(Function<Locale, ? extends T> creator) {
    this.creator = creator;
  }

  public static <S> LocaleLocal<S> withInitial(Function<Locale, ? extends S> creator) {
    return new LocaleLocal<>(creator);
  }

  public T get(Locale locale) {
    return map.computeIfAbsent(locale, creator);
  }
}
