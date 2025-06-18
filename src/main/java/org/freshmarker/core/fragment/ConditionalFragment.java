package org.freshmarker.core.fragment;

import ftl.Node;
import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.ProcessException;
import org.freshmarker.core.ReduceContext;
import org.freshmarker.core.ReduceException;
import org.freshmarker.core.WrongTypeException;
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
        content.process(context);
    }

    @Override
    public ConditionalFragment reduce(ReduceContext context) {
        try {
            Fragment reduce = content.reduce(context);
            if (reduce == content) {
                return this;
            }
            context.getStatus().replaced().incrementAndGet();
            return new ConditionalFragment(conditional, reduce, node);
        } catch (WrongTypeException e) {
            throw new ReduceException(e.getMessage(), node, e);
        } catch (ProcessException e) {
            return this;
        }
    }

    @Override
    public int getSize() {
        return content.getSize() + 1;
    }

    @Override
    public <R> R accept(TemplateVisitor<R> visitor) {
        return visitor.visit(this, conditional, content);
    }
}
