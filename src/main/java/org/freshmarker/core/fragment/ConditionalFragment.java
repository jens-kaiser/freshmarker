package org.freshmarker.core.fragment;

import ftl.Node;
import org.freshmarker.core.Environment;
import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.ReduceContext;
import org.freshmarker.core.environment.VariableEnvironment;
import org.freshmarker.core.model.TemplateObject;

import java.util.Objects;

public class ConditionalFragment implements Fragment {

    private final TemplateObject conditional;
    private final Fragment content;
    private final Node node;

    public ConditionalFragment(TemplateObject conditional, Fragment content, Node node) {
        this.conditional = Objects.requireNonNull(conditional);
        this.content = Objects.requireNonNull(content);
        this.node = node;
    }

    public Node getNode() {
        return node;
    }

    public TemplateObject getConditional() {
        return conditional;
    }

    public Fragment getContent() {
        return content;
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
        try {
            Fragment reduce = content.reduce(context);
            if (reduce == content) {
                return this;
            }
            context.getStatus().changed().incrementAndGet();
            return new ConditionalFragment(conditional, reduce, node);
        } catch (RuntimeException e) {
            return this;
        }
    }

    @Override
    public int getSize() {
        return content.getSize() + 1;
    }
}
