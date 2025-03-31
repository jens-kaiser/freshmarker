package org.freshmarker.api.extension;

import org.freshmarker.core.buildin.BuiltIn;
import org.freshmarker.core.buildin.BuiltInKey;

import java.util.Map;

/**
 * An {@link Extension} to add new built-ins.
 */
public interface BuiltInProvider extends Extension {
    /**
     * Returns a map of built-ins
     * @return a map of built-ins
     */
    Map<BuiltInKey, BuiltIn> provideBuiltIns();
}
