package org.freshmarker.core.model;

import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.ProcessException;
import org.freshmarker.core.ReduceContext;
import org.freshmarker.core.UnsupportedBuiltInException;

import java.util.ArrayList;
import java.util.List;

public final class TemplateBuiltIn implements TemplateExpression {
    private final String name;
    private final TemplateObject expression;
    private final List<TemplateObject> parameter;
    private final boolean ignoreOptionalNull;
    private final boolean ignoreNull;

    public TemplateBuiltIn(String name, TemplateObject expression, List<TemplateObject> parameter, boolean ignoreOptionalNull,
                           boolean ignoreNull) {
        this.name = name;
        this.expression = expression;
        this.parameter = parameter;
        this.ignoreOptionalNull = ignoreOptionalNull;
        this.ignoreNull = ignoreNull;
    }

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
        return visitor.visit(this, name, expression, parameter);
    }

    @Override
    public TemplateObject reduce(ReduceContext context) {
        TemplateObject result = expression.reduce(context);
        boolean allReduced = true;
        List<TemplateObject> reducedParameters = new ArrayList<>();
        for (TemplateObject p : parameter) {
            TemplateObject reducedParameter = p.reduce(context);
            if (reducedParameter == p) {
                allReduced = false;
            }
            reducedParameters.add(reducedParameter);
        }

        try {
            return context.getBuiltIn(result.getClass(), name).apply(result, reducedParameters, context);
        } catch (RuntimeException e) {
            if (result == expression && !allReduced) {
                return this;
            }
            return new TemplateBuiltIn(name, result, reducedParameters, ignoreOptionalNull, ignoreNull);
        }
    }
}
