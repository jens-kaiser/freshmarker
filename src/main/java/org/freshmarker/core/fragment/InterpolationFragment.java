package org.freshmarker.core.fragment;

import ftl.ast.Interpolation;
import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.ProcessException;
import org.freshmarker.core.ReduceContext;
import org.freshmarker.core.ReduceException;
import org.freshmarker.core.UnsupportedBuiltInException;
import org.freshmarker.core.WrongTypeException;
import org.freshmarker.core.model.TemplateMarkup;
import org.freshmarker.core.model.primitive.TemplateString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class InterpolationFragment implements Fragment {

    private static final Logger log = LoggerFactory.getLogger(InterpolationFragment.class);

    private final TemplateMarkup expression;
    private final Interpolation ftl;
    private final boolean partialExpressionReduction;

    public InterpolationFragment(TemplateMarkup expression, Interpolation ftl, boolean partialExpressionReduction) {
        this.expression = expression;
        this.ftl = ftl;
        this.partialExpressionReduction = partialExpressionReduction;
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
        TemplateMarkup reduced = partialExpressionReduction ? expression.reduce(context) : expression;
        try {
            TemplateString templateObject = reduced.evaluate(context, TemplateString.class);
            context.getStatus().replaced().incrementAndGet();
            return new ConstantFragment(templateObject.getValue());
        } catch (WrongTypeException e) {
            throw new ReduceException(e.getMessage(), ftl, e);
        } catch (ProcessException e) {
            if (partialExpressionReduction && reduced != expression) {
                context.getStatus().expressions().add(expression);
                context.getStatus().expressions().add(reduced);
                log.debug("Reduced: {} to {}", expression, reduced);
                return new InterpolationFragment(reduced, ftl, true);
            }
            return this;
        }
    }

    @Override
    public <R> R accept(TemplateVisitor<R> visitor) {
        return visitor.visit(this, expression);
    }
}
