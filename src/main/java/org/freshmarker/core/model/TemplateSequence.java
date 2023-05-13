package org.freshmarker.core.model;

import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.model.primitive.TemplateNumber;

import java.util.List;

public interface TemplateSequence extends TemplateObject {

  TemplateObject get(ProcessContext context, int index);

  TemplateNumber size(ProcessContext context);

  List<Object> getSequence(ProcessContext context);
}
