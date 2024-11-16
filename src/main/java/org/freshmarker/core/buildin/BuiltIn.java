package org.freshmarker.core.buildin;

import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.model.TemplateObject;

import java.util.List;

public interface BuiltIn {
    TemplateObject apply(TemplateObject value, List<TemplateObject> parameters, ProcessContext context);
}
