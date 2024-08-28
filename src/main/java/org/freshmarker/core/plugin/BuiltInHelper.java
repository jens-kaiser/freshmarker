package org.freshmarker.core.plugin;

import org.freshmarker.core.ProcessException;
import org.freshmarker.core.model.TemplateObject;

import java.util.List;

public final class BuiltInHelper {
    public BuiltInHelper() {
        super();
    }

    public static String generateSnakeCase(String name) {
        StringBuilder result = new StringBuilder();
        for (char c : name.toCharArray()) {
            if (Character.isUpperCase(c)) {
                result.append("_").append(Character.toLowerCase(c));
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }

    public static void checkParametersLength(List<TemplateObject> parameters, int length) {
        if (parameters.size() != length) {
            throw new ProcessException("invalid parameter count:" + parameters.size());
        }
    }
}
