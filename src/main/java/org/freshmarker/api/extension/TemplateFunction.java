package org.freshmarker.api.extension;

import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.model.TemplateObject;

import java.util.List;

public interface TemplateFunction {

    TemplateObject execute(ProcessContext context, List<TemplateObject> args);
}
