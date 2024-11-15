package org.freshmarker.core.plugin;

import org.freshmarker.core.ProcessException;
import org.freshmarker.core.model.TemplateObject;

import java.util.List;

public final class BuiltInHelper {
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
}
