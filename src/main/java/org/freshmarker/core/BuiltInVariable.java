package org.freshmarker.core;

import org.freshmarker.core.model.TemplateObject;

import java.util.function.Function;

public interface BuiltInVariable extends Function<ProcessContext, TemplateObject> {
}
