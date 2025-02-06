package org.freshmarker.core.fragment;

import ftl.Node;
import org.freshmarker.core.Environment;
import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.ReduceContext;
import org.freshmarker.core.environment.ReducingVariableEnvironment;
import org.freshmarker.core.environment.VariableEnvironment;
import org.freshmarker.core.model.TemplateObject;

import java.util.Objects;

public record ConditionalFragment(TemplateObject conditional, Fragment content, Node node) implements Fragment {

    public ConditionalFragment(TemplateObject conditional, Fragment content, Node node) {
        this.conditional = Objects.requireNonNull(conditional);
        this.content = Objects.requireNonNull(content);
        this.node = node;
    }

    @Override
    public void process(ProcessContext context) {
        Environment environment = context.getEnvironment();
        try {
            context.setEnvironment(new VariableEnvironment(environment));
            content.process(context);
        } finally {
            context.setEnvironment(environment);
        }
    }

    @Override
    public ConditionalFragment reduce(ReduceContext context) {
        Environment environment = context.getEnvironment();
        try {
            context.setEnvironment(new ReducingVariableEnvironment(environment));
            Fragment reduce = content.reduce(context);
            if (reduce == content) {
                return this;
            }
            context.getStatus().changed().incrementAndGet();
            return new ConditionalFragment(conditional, reduce, node);
        } catch (RuntimeException e) {
            return this;
        } finally {
            context.setEnvironment(environment);
        }
    }

    @Override
    public int getSize() {
        return content.getSize() + 1;
    }

    @Override
    public void accept(TemplateVisitor visitor) {
        visitor.visit(this, conditional, content);
    }
}
