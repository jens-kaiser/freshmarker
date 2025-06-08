package org.freshmarker.core.model;

import org.freshmarker.core.ProcessContext;

import java.util.List;

public interface TemplateSequence<T> extends TemplateObject {

  int size(ProcessContext context);

  List<T> sequence();
}
