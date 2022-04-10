package org.freshmarker.core.model;

import org.freshmarker.core.ProcessContext;

public interface TemplateMap extends TemplateObject {

  TemplateObject get(ProcessContext context, String name);
}
