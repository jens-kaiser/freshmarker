package org.freshmarker.core.fragment;

import ftl.Node;
import org.freshmarker.core.Environment;
import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.ProcessException;
import org.freshmarker.core.ReduceContext;
import org.freshmarker.core.model.TemplateObject;

public record VariableFragment(String name, TemplateObject expression, boolean exists, Node node) implements Fragment {

    @Override
    public void process(ProcessContext context) {
        Environment environment = context.getEnvironment();
        if (exists) {
            environment.setVariable(name, expression.evaluateToObject(context));
        } else {
            if (environment.checkVariable(name)) {
                throw new ProcessException("variable " + name + " must not exist", node);
            }
            environment.createVariable(name, expression.evaluateToObject(context));
        }
    }

    @Override
    public Fragment reduce(ReduceContext context) {
        Environment environment = context.getEnvironment();
        try {
            TemplateObject value = expression.evaluateToObject(context);
            if (value.isNull()) {
                return this;
            }
            if (exists) {
                if (environment.getVariable(name) == null) {
                    return this;
                }
                environment.setVariable(name, value);
            } else {
                if (environment.checkVariable(name)) {
                    return this;
                }
                environment.createVariable(name, value);
            }
            context.getStatus().changed().incrementAndGet();
            return new VariableFragment(name, value, exists, node);
        } catch (RuntimeException e) {
            return this;
        }
    }

    @Override
    public <R> R accept(TemplateVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
