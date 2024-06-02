package org.freshmarker.core.model;

import org.freshmarker.core.ProcessContext;

import java.util.Map;

public interface TemplateMap extends TemplateObject {

    TemplateObject get(ProcessContext context, String name);

    @Override
    default boolean isMap() {
        return true;
    }

    Map<String, Object> map();
}
