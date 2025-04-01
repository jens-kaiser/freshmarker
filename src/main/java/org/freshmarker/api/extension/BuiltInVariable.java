package org.freshmarker.api.extension;

import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.model.TemplateObject;

import java.util.function.Function;

public interface BuiltInVariable extends Function<ProcessContext, TemplateObject> {
}