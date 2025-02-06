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

public class SwitchFragment extends AbstractConditionalFragment {
    private static final Logger log = LoggerFactory.getLogger(SwitchFragment.class);

    private final TemplateObject switchExpression;
    private final Node node;

    public SwitchFragment(TemplateObject switchExpression, Node node) {
        this.switchExpression = switchExpression;
        this.node = node;
    }

    private SwitchFragment(TemplateObject switchExpression, Node node, List<ConditionalFragment> fragments, Fragment endFragment) {
        super(fragments, endFragment);
        this.switchExpression = switchExpression;
        this.node = node;
    }

    public void addDefaultFragment(Fragment fragment) {
        endFragment = fragment;
    }

    public void process(ProcessContext context) {
        TemplatePrimitive<?> switchValue = evaluatePrimitive(this.switchExpression, context, node);
        for (ConditionalFragment fragment : fragments) {
            TemplateObject evaluated = evaluateConditional(fragment.conditional(), context, fragment.node());
            if (isFound(evaluated, switchValue)) {
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
                if (isFound(evaluated, switchValue)) {
                    return fragment.reduce(context);
                }
            }
            return endFragment.reduce(context);
        } catch (RuntimeException e) {
            log.info("cannot reduce: {}", e.getMessage(), e);
        }
        return new SwitchFragment(switchExpression, node, fragments.stream().map(f -> f.reduce(context)).toList(), endFragment.reduce(context));
    }

    private boolean isFound(TemplateObject evaluated, TemplatePrimitive<?> switchValue) {
        if (evaluated instanceof TemplatePrimitive<?> primitive) {
            return primitive.equals(switchValue);
        }
        throw new ProcessException("invalid value: " + evaluated, node);
    }

    @Override
    public void accept(TemplateVisitor visitor) {
        visitor.visit(this);
    }
}
