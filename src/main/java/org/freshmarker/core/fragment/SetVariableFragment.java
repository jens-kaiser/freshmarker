package org.freshmarker.core.fragment;

import ftl.Node;
import org.freshmarker.core.Environment;
import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.ReduceContext;
import org.freshmarker.core.model.TemplateObject;

public record SetVariableFragment(String name, TemplateObject expression, Node node) implements Fragment {

    @Override
    public void process(ProcessContext context) {
        Environment environment = context.getEnvironment();
        environment.setVariable(name, expression.evaluateToObject(context));
    }

    @Override
    public Fragment reduce(ReduceContext context) {
        Environment environment = context.getEnvironment();
        try {
            TemplateObject value = expression.evaluateToObject(context);
            if (value.isNull()) {
                return this;
            }
            if (environment.getVariable(name) == null) {
                return this;
            }
            environment.setVariable(name, value);
            context.getStatus().changed().incrementAndGet();
            return new SetVariableFragment(name, value, node);
        } catch (RuntimeException e) {
            return this;
        }
    }

    @Override
    public <R> R accept(TemplateVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
