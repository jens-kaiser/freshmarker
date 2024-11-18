package org.freshmarker.core.providers;

import org.freshmarker.core.model.TemplateObject;

import java.util.Set;

public interface TemplateObjectMapper {
    TemplateObject mapObject(Object object);
    Set<Class<?>> getChecks();
}
