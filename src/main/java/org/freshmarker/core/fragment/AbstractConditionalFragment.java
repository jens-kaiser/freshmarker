package org.freshmarker.core.fragment;

import ftl.Node;
import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.ProcessException;
import org.freshmarker.core.UnsupportedBuiltInException;
import org.freshmarker.core.WrongTypeException;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.model.primitive.TemplatePrimitive;

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
}
