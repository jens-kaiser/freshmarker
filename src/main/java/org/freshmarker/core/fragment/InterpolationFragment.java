package org.freshmarker.core.fragment;

import ftl.ast.Interpolation;
import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.ProcessException;
import org.freshmarker.core.ReduceContext;
import org.freshmarker.core.UnsupportedBuiltInException;
import org.freshmarker.core.WrongTypeException;
import org.freshmarker.core.model.TemplateMarkup;
import org.freshmarker.core.model.primitive.TemplateString;

import java.io.IOException;

public class InterpolationFragment implements Fragment {

    private final TemplateMarkup expression;
    private final Interpolation ftl;

    public InterpolationFragment(TemplateMarkup expression, Interpolation ftl) {
        this.expression = expression;
        this.ftl = ftl;
    }

    @Override
    public void process(ProcessContext context) {
        try {
            context.getWriter().write(expression.evaluate(context, TemplateString.class).getValue());
        } catch (UnsupportedBuiltInException e) {
            throw new UnsupportedBuiltInException(e.getMessage(), ftl, e);
        } catch (WrongTypeException e) {
            throw new WrongTypeException(e.getMessage(), ftl, e);
        } catch (IOException | ProcessException e) {
            throw new ProcessException(e.getMessage(), ftl, e);
        }
    }

    @Override
    public Fragment reduce(ReduceContext context) {
        try {
            TemplateString templateObject = expression.evaluate(context, TemplateString.class);
            context.getStatus().changed().incrementAndGet();
            return new ConstantFragment(templateObject.getValue());
        } catch (WrongTypeException e) {
            throw new WrongTypeException(e.getMessage(), ftl, e);
        } catch (ProcessException e) {
            return this;
        }
    }

    @Override
    public void accept(TemplateVisitor visitor) {
        visitor.visit(this, expression);
    }
}
