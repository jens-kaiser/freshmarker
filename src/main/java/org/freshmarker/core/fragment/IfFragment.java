package org.freshmarker.core.fragment;

import ftl.Node;
import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.ProcessException;
import org.freshmarker.core.ReduceContext;
import org.freshmarker.core.ReduceException;
import org.freshmarker.core.WrongTypeException;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.model.primitive.TemplateBoolean;

import java.util.List;

public class IfFragment extends AbstractConditionalFragment {

    protected final List<ConditionalFragment> fragments;

    public IfFragment(List<ConditionalFragment> fragments, Fragment endFragment, Node node) {
        super(endFragment, node);
        this.fragments = fragments;
    }

    public void addElseFragment(Fragment fragment) {
        endFragment = fragment;
    }

    public void addFragment(ConditionalFragment fragment) {
        fragments.add(fragment);
    }

    @Override
    public int getSize() {
        return fragments.stream().mapToInt(Fragment::getSize).sum() + endFragment.getSize() + 1;
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
        TemplateObject conditional = evaluateConditional(conditionalFragment.conditional(), context, conditionalFragment.node());
        context.reductionCheck(conditional);
        return TemplateBoolean.TRUE == conditional;
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
        } catch (WrongTypeException e) {
            throw new ReduceException(e.getMessage(), node, e);
        } catch (ProcessException ignored) {
            return new IfFragment(reduceConditionals(context, fragments), endFragment.reduce(context), node);
        }
    }

    @Override
    public <R> R accept(TemplateVisitor<R> visitor) {
        return visitor.visit(this, fragments, endFragment);
    }
}
