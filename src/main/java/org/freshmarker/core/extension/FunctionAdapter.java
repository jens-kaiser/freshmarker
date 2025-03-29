package org.freshmarker.core.extension;

import org.freshmarker.api.NamedFunction;
import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.directive.TemplateFunction;
import org.freshmarker.core.model.TemplateObject;

import java.util.List;

public class FunctionAdapter implements NamedFunction {
    private final String name;
    private final TemplateFunction function;

    public FunctionAdapter(String name, TemplateFunction function) {
        this.name = name;
        this.function = function;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public TemplateObject execute(ProcessContext context, List<TemplateObject> args) {
        return function.execute(context, args);
    }
}
