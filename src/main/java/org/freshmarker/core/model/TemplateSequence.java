package org.freshmarker.core.model;

import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.model.primitive.TemplateNumber;

public interface TemplateSequence extends TemplateObject {

  TemplateObject get(ProcessContext context, int index);

  TemplateNumber size(ProcessContext context);
}
