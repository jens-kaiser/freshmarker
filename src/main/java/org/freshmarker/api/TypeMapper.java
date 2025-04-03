package org.freshmarker.api;

import org.freshmarker.core.model.TemplateObject;

import java.util.function.Function;

public interface TypeMapper extends Function<Object, TemplateObject> {
}
