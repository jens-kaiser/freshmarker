package org.freshmarker.core.buildin;

import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.model.TemplateObject;

import java.util.List;

/**
 * Built-ins are called up within interpolations to the current value of the interpolation.
 */
public interface BuiltIn {
    /**
     * The current value is processed by the Built-In and a changed object is returned.
     *
     * @param value the object to be processed
     * @param parameters the current parameters of the Built-In
     * @param context the current process context
     * @return the possibly changed object
     */
    TemplateObject apply(TemplateObject value, List<TemplateObject> parameters, ProcessContext context);
}
