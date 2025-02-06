package org.freshmarker.core.fragment;

import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.ReduceContext;
import org.freshmarker.core.model.primitive.TemplateBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class IfFragment extends AbstractConditionalFragment {
    private static final Logger log = LoggerFactory.getLogger(IfFragment.class);

    public IfFragment(List<ConditionalFragment> fragments, Fragment endFragment) {
        super(fragments, endFragment);
    }

    public IfFragment() {
        super();
    }

    public void addElseFragment(Fragment fragment) {
        endFragment = fragment;
    }

    @Override
    public void process(ProcessContext context) {
        for (ConditionalFragment fragment : fragments) {
            if (filterByConditional(context, fragment)) {
                fragment.process(context);
                return;
            }
        }
        endFragment.process(context);
    }

    private boolean filterByConditional(ProcessContext context, ConditionalFragment conditionalFragment) {
        return TemplateBoolean.TRUE == evaluateConditional(conditionalFragment.conditional(), context, conditionalFragment.node());
    }

    @Override
    public Fragment reduce(ReduceContext context) {
        try {
            for (ConditionalFragment fragment : fragments) {
                if (filterByConditional(context, fragment)) {
                    return fragment.reduce(context).content();
                }
            }
            return endFragment.reduce(context);
        } catch (RuntimeException e) {
            log.info("cannot reduce: {}", e.getMessage(), e);
        }
        return new IfFragment(fragments.stream().map(f -> f.reduce(context)).toList(), endFragment.reduce(context));
    }

    @Override
    public void accept(TemplateVisitor visitor) {
        visitor.visit(this, fragments, endFragment);
    }
}
