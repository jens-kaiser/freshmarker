package org.freshmarker.core.model.builtin;

import org.freshmarker.api.BuiltIn;
import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.ProcessException;
import org.freshmarker.core.UnsupportedBuiltInException;
import org.freshmarker.core.buildin.BuiltInKey;
import org.freshmarker.core.model.TemplateNull;
import org.freshmarker.core.model.TemplateObject;

import java.util.List;

public class HookedBuiltIn implements TemplateObject {
    private final List<TemplateObject> parameter;
    private final TemplateObject expression;
    private final BuiltIn builtIn;
    private final BuiltInKey builtInKey;
    final boolean ignoreOptionalEmpty;
    final boolean ignoreNull;

    public HookedBuiltIn(TemplateObject expression, BuiltInKey builtInKey, BuiltIn builtIn, List<TemplateObject> parameter, boolean ignoreOptionalEmpty, boolean ignoreNull) {
        this.builtInKey = builtInKey;
        this.builtIn = builtIn;
        this.parameter = parameter;
        this.expression = expression;
        this.ignoreOptionalEmpty = ignoreOptionalEmpty;
        this.ignoreNull = ignoreNull;
    }

    @Override
    public TemplateObject evaluateToObject(ProcessContext context) {
        TemplateObject result = expression.evaluateToObject(context);
        if (result == TemplateNull.NULL_OPTIONAL && ignoreOptionalEmpty) {
            return result;
        }
        if (result == TemplateNull.NULL && ignoreNull) {
            return result;
        }
        if (builtInKey.getType() != result.getClass()) {
            throw new UnsupportedBuiltInException("unsupported builtin '" + builtInKey.getName() + "' for " + result.getClass().getSimpleName());
        }
        try {
            return builtIn.apply(result, parameter, context);
        } catch (UnsupportedBuiltInException e) {
            throw e;
        } catch (RuntimeException e) {
            throw new ProcessException(e.getMessage(), e);
        }
    }
}
