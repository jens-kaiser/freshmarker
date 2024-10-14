package org.freshmarker.core.fragment;

import ftl.Node;
import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.ProcessException;
import org.freshmarker.core.UnsupportedBuiltInException;
import org.freshmarker.core.WrongTypeException;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.model.primitive.TemplatePrimitive;

import java.util.LinkedList;
import java.util.List;

public abstract class AbstractConditionalFragment implements Fragment {
    protected final List<ConditionalFragment> fragments;
    protected Fragment endFragment;

    protected AbstractConditionalFragment() {
        this(new LinkedList<>(), ConstantFragment.EMPTY);
    }

    protected AbstractConditionalFragment(List<ConditionalFragment> fragments, Fragment endFragment) {
        this.fragments = fragments;
        this.endFragment = endFragment;
    }

    public void addFragment(ConditionalFragment fragment) {
        fragments.add(fragment);
    }

    protected TemplatePrimitive<?> evaluatePrimitive(TemplateObject conditional, ProcessContext context, Node node) {
        try {
            TemplateObject templateObject = conditional.evaluateToObject(context);
            if (templateObject instanceof TemplatePrimitive<?> primitive) {
                return primitive;
            }
            throw new WrongTypeException("not a primitive type", node);
        } catch (UnsupportedBuiltInException e) {
            throw new UnsupportedBuiltInException(e.getMessage(), node, e);
        } catch (WrongTypeException e) {
            throw new WrongTypeException(e.getMessage(), node, e);
        } catch (ProcessException e) {
            throw new ProcessException(e.getMessage(), node, e);
        }
    }

    @Override
    public int getSize() {
        return fragments.stream().mapToInt(Fragment::getSize).sum() + endFragment.getSize() + 1;
    }
}
