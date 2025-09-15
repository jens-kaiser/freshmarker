package org.freshmarker.core.model.builtin;

import org.freshmarker.api.BuiltIn;
import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.ProcessException;
import org.freshmarker.core.ReduceContext;
import org.freshmarker.core.UnsupportedBuiltInException;
import org.freshmarker.core.buildin.BuiltInKey;
import org.freshmarker.core.model.TemplateNull;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.model.TemplateObjectVisitor;

import java.util.List;

public class HookedBuiltIn extends AbstractBuiltIn {
    private final BuiltIn builtIn;
    private final BuiltInKey builtInKey;

    public HookedBuiltIn(TemplateObject expression, BuiltInKey builtInKey, BuiltIn builtIn, List<TemplateObject> parameter, boolean ignoreOptionalEmpty, boolean ignoreNull) {
        super(builtInKey.getName(), expression, parameter, ignoreOptionalEmpty, ignoreNull);
        this.builtInKey = builtInKey;
        this.builtIn = builtIn;
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
        } catch (RuntimeException e) {
            throw new ProcessException(e.getMessage(), e);
        }
    }

    @Override
    public TemplateObject reduce(ReduceContext context) {
        TemplateObject result = expression.reduce(context);
        ReducedParameters reducedParameters = reduceParameters(context);

        boolean noneReduced = reducedParameters.noneReduced();

        if (builtInKey.getType() != result.getClass()) {
            return noneReduced ? this : new HookedBuiltIn(result, builtInKey, builtIn, reducedParameters.parameters(), ignoreOptionalEmpty, ignoreNull);
        }

        try {
            return builtIn.apply(result, reducedParameters.parameters(), context);
        } catch (RuntimeException e) {
            if (result == expression && noneReduced) {
                return this;
            }
            return new HookedBuiltIn(result, builtInKey, builtIn, reducedParameters.parameters(), ignoreOptionalEmpty, ignoreNull);
        }
    }

    @Override
    public <R> R accept(TemplateObjectVisitor<R> visitor) {
        return visitor.visit(this, builtInKey.getName(), expression, parameter);
    }
}
