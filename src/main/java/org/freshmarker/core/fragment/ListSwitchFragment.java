package org.freshmarker.core.fragment;

import ftl.Node;
import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.ProcessException;
import org.freshmarker.core.ReduceContext;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.model.primitive.TemplatePrimitive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ListSwitchFragment extends AbstractConditionalFragment implements SwitchFragment {
    private static final Logger log = LoggerFactory.getLogger(ListSwitchFragment.class);

    private final TemplateObject switchExpression;
    private final List<ConditionalFragment> fragments;

    public ListSwitchFragment(TemplateObject switchExpression, Node node, List<ConditionalFragment> fragments, Fragment endFragment) {
        super(endFragment, node);
        this.switchExpression = switchExpression;
        this.fragments = fragments;
    }

    public void process(ProcessContext context) {
        TemplatePrimitive<?> switchValue = evaluatePrimitive(this.switchExpression, context, node);
        for (ConditionalFragment fragment : fragments) {
            TemplateObject evaluated = evaluateConditional(fragment.conditional(), context, fragment.node());
            if (isFound(evaluated, switchValue, fragment.node())) {
                fragment.process(context);
                return;
            }
        }
        endFragment.process(context);
    }

    @Override
    public Fragment reduce(ReduceContext context) {
        try {
            TemplatePrimitive<?> switchValue = evaluatePrimitive(this.switchExpression, context, node);
            for (ConditionalFragment fragment : fragments) {
                TemplateObject evaluated = evaluateConditional(fragment.conditional(), context, fragment.node());
                if (isFound(evaluated, switchValue, fragment.node())) {
                    return fragment.content().reduce(context);
                }
            }
            return endFragment.reduce(context);
        } catch (RuntimeException e) {
            log.info("cannot reduce: {}", e.getMessage(), e);
            return new ListSwitchFragment(switchExpression, node, fragments.stream().map(f -> f.reduce(context)).toList(), endFragment.reduce(context));
        }
    }

    private boolean isFound(TemplateObject evaluated, TemplatePrimitive<?> switchValue, Node expression) {
        if (evaluated instanceof TemplatePrimitive<?> primitive) {
            return primitive.equals(switchValue);
        }
        throw new ProcessException("non primitive type: " + evaluated.getModelType(), expression);
    }

    @Override
    public <R> R accept(TemplateVisitor<R> visitor) {
        return visitor.visit(this, switchExpression, fragments, endFragment);
    }

    @Override
    public int getSize() {
        return fragments.stream().mapToInt(Fragment::getSize).sum() + endFragment.getSize() + 1;
    }
}
