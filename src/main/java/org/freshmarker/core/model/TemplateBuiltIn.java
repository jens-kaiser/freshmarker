package org.freshmarker.core.model;

import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.ProcessException;
import org.freshmarker.core.ReduceContext;
import org.freshmarker.core.UnsupportedBuiltInException;
import org.freshmarker.core.model.builtin.AbstractBuiltIn;

import java.util.List;

public final class TemplateBuiltIn extends AbstractBuiltIn {
    public TemplateBuiltIn(String name, TemplateObject expression, List<TemplateObject> parameter, boolean ignoreOptionalNull,
                           boolean ignoreNull) {
        super(name, expression, parameter, ignoreOptionalNull, ignoreNull);
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
        return visitor.visit(this, name, expression, parameter);
    }

    @Override
    public TemplateObject reduce(ReduceContext context) {
        TemplateObject result = expression.reduce(context);
        ReducedParameters reducedParameters = reduceParameters(context);

        try {
            return context.getBuiltIn(result.getClass(), name).apply(result, reducedParameters.parameters(), context);
        } catch (RuntimeException e) {
            if (result == expression && reducedParameters.noneReduced()) {
                return this;
            }
            return new TemplateBuiltIn(name, result, reducedParameters.parameters(), ignoreOptionalEmpty, ignoreNull);
        }
    }
}
