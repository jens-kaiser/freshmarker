package org.freshmarker.core.model;

import org.freshmarker.core.Environment;
import org.freshmarker.core.model.primitive.TemplateNumber;

public interface TemplateSequence extends TemplateObject {

  TemplateObject get(Environment environment, int index);

  TemplateNumber size(Environment environment);
}
