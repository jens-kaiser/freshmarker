package org.freshmarker.core.model;

import org.freshmarker.core.Environment;

public interface TemplateMap extends TemplateObject{

  TemplateObject get(Environment environment, String name);
}
