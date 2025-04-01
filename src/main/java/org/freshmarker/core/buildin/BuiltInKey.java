package org.freshmarker.core.buildin;

import org.freshmarker.core.model.TemplateObject;

import java.util.Objects;

/**
 * Key used for the internal management of built-ins.
 */
@Deprecated(since = "1.8.0", forRemoval = true)
public final class BuiltInKey extends org.freshmarker.api.extension.BuiltInKey {

    public BuiltInKey(Class<? extends TemplateObject> type, String name) {
        super(type, name);
    }
}
