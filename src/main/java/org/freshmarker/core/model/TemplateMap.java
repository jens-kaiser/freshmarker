package org.freshmarker.core.model;

import org.freshmarker.core.ProcessContext;

import java.util.Map;

public interface TemplateMap extends TemplateObject {

    TemplateObject get(ProcessContext context, String name);

    Map<String, Object> map();
}
