package org.freshmarker.api;

import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.model.primitive.TemplateString;

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

    static BuiltIn identity() {
        return (x, y, e) -> x;
    }

    static BuiltIn string() {
        return (x, y, e) -> new TemplateString(x.toString());
    }
}
