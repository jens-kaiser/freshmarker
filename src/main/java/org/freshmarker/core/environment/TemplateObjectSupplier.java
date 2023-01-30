package org.freshmarker.core.environment;

import java.util.function.Supplier;

public interface TemplateObjectSupplier<T> extends Supplier<T> {

  static <S> TemplateObjectSupplier<S> of(Supplier<S> supplier) {
    return supplier::get;
  }
}
