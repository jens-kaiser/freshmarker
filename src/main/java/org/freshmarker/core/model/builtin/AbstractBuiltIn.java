package org.freshmarker.core.model.builtin;

import org.freshmarker.core.ReduceContext;
import org.freshmarker.core.model.TemplateExpression;
import org.freshmarker.core.model.TemplateObject;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractBuiltIn implements TemplateExpression {
    protected final String name;
    protected final TemplateObject expression;
    protected final List<TemplateObject> parameter;
    protected final boolean ignoreOptionalEmpty;
    protected final boolean ignoreNull;

    protected record ReducedParameters(List<TemplateObject> parameters, boolean noneReduced) {
    }

    protected AbstractBuiltIn(String name, TemplateObject expression, List<TemplateObject> parameter, boolean ignoreOptionalEmpty, boolean ignoreNull) {
        this.name = name;
        this.expression = expression;
        this.parameter = parameter;
        this.ignoreOptionalEmpty = ignoreOptionalEmpty;
        this.ignoreNull = ignoreNull;
    }

    protected ReducedParameters reduceParameters(ReduceContext context) {
        List<TemplateObject> reducedParameters = new ArrayList<>();
        boolean noneReduced = true;
        for (TemplateObject p : parameter) {
            TemplateObject reducedParameter = p.reduce(context);
            if (reducedParameter != p) {
                noneReduced = false;
            }
            reducedParameters.add(reducedParameter);
        }
        return new ReducedParameters(reducedParameters, noneReduced);
    }
}
