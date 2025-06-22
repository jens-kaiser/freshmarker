package org.freshmarker.core.fragment;

import ftl.Node;
import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.ProcessException;
import org.freshmarker.core.ReduceContext;
import org.freshmarker.core.UnsupportedBuiltInException;
import org.freshmarker.core.WrongTypeException;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.model.primitive.TemplatePrimitive;

import java.util.List;

public abstract class AbstractConditionalFragment implements Fragment {
    protected Fragment endFragment;
    protected final Node node;

    protected AbstractConditionalFragment(Fragment endFragment, Node node) {
        this.endFragment = endFragment;
        this.node = node;
    }

    protected TemplatePrimitive<?> evaluatePrimitive(TemplateObject conditional, ProcessContext context, Node node) {
        TemplateObject templateObject = evaluateConditional(conditional, context, node);
        if (templateObject instanceof TemplatePrimitive<?> primitive) {
            return primitive;
        }
        throw new WrongTypeException("not a primitive type", node);
    }

    protected TemplateObject evaluateConditional(TemplateObject conditional, ProcessContext context, Node node) {
        try {
            TemplateObject templateObject = conditional.evaluateToObject(context);
            context.reductionCheck(templateObject);
            return templateObject;
        } catch (UnsupportedBuiltInException e) {
            throw new UnsupportedBuiltInException(e.getMessage(), node, e);
        } catch (WrongTypeException e) {
            throw new WrongTypeException(e.getMessage(), node, e);
        } catch (ProcessException e) {
            throw new ProcessException(e.getMessage(), node, e);
        }
    }

    protected List<ConditionalFragment> reduceConditionals(ReduceContext context, List<ConditionalFragment> fragments) {
        return fragments.stream().map(fragment -> fragment.reduce(context)).toList();
    }

    protected ConditionalFragment reduceConditional(ReduceContext context, ConditionalFragment fragment) {
        try {
            return fragment.reduce(context);
        } catch (ProcessException ignored) {
            return fragment;
        }
    }

    protected Fragment reduceFragment(ReduceContext context, Fragment fragment) {
        try {
            return fragment.reduce(context);
        } catch (ProcessException ignored) {
            return fragment;
        }
    }

}
