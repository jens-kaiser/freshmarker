package org.freshmarker.core.model;

import org.freshmarker.core.Environment;
import org.freshmarker.core.model.primitive.TemplateObject;

public interface TemplateMap extends TemplateObject{

  TemplateObject get(Environment environment, String name);
}
