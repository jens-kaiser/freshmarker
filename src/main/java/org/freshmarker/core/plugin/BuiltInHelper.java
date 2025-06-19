package org.freshmarker.core.plugin;

import org.freshmarker.api.BuiltIn;
import org.freshmarker.core.ProcessException;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.model.primitive.TemplateBoolean;

import java.util.List;

public final class BuiltInHelper {

    private static final BuiltIn ALWAYS_TRUE = BuiltIn.value(TemplateBoolean.TRUE);
    private static final BuiltIn ALWAYS_FALSE = BuiltIn.value(TemplateBoolean.FALSE);

    private BuiltInHelper() {
        super();
    }

    public static void checkParametersLength(List<TemplateObject> parameters, int length) {
        if (parameters.size() != length) {
            throw new ProcessException("invalid parameter count:" + parameters.size());
        }
    }

    public static void checkParametersLength(List<TemplateObject> parameters, int first, int second) {
        if (parameters.size() != first && parameters.size() != second) {
            throw new ProcessException("invalid parameter count:" + parameters.size());
        }
    }

    public static BuiltIn alwaysTrue() {
        return ALWAYS_TRUE;
    }

    public static BuiltIn alwaysFalse() {
        return ALWAYS_FALSE;
    }
}
