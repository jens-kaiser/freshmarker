package org.freshmarker.core.model;

import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.ProcessException;
import org.freshmarker.core.UnsupportedBuiltInException;

import java.util.List;

public record TemplateBuiltIn(String name, TemplateObject expression, List<TemplateObject> parameter, boolean ignoreOptionalNull,
                              boolean ignoreNull) implements TemplateExpression {
    @Override
    public TemplateObject evaluateToObject(ProcessContext context) {
        TemplateObject result = expression.evaluateToObject(context);
        if (result == TemplateNull.NULL_OPTIONAL && ignoreOptionalNull) {
            return result;
        }
        if (result == TemplateNull.NULL && ignoreNull) {
            return result;
        }
        try {
            return context.getBuiltIn(result.getClass(), name).apply(result, parameter, context);
        } catch (UnsupportedBuiltInException e) {
            throw e;
        } catch (RuntimeException e) {
            throw new ProcessException(e.getMessage(), e);
        }
    }

    @Override
    public <R> R accept(TemplateObjectVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
