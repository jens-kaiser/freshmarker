package org.freshmarker.api.extension;

import org.freshmarker.api.BuiltIn;
import org.freshmarker.core.model.TemplateObject;

/**
 * An {@link Extension} to add new built-ins.
 */
public interface BuiltInProvider extends Extension {
    /**
     * Returns a register of built-ins
     * @return a register of built-ins
     */
   Register<Class<? extends TemplateObject>, String, BuiltIn> provideBuiltInRegister();
}
