package org.freshmarker.core.model;

import org.freshmarker.core.ProcessContext;

import java.util.List;

public interface TemplateSequence extends TemplateObject {

  TemplateObject get(ProcessContext context, int index);

  int size(ProcessContext context);

  List<Object> getSequence(ProcessContext context);
}
