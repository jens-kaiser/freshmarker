package org.freshmarker.core.fragment;

import ftl.ast.ListInstruction;
import org.freshmarker.core.Environment;
import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.ProcessException;
import org.freshmarker.core.ReduceContext;
import org.freshmarker.core.ReduceException;
import org.freshmarker.core.ReductionFeature;
import org.freshmarker.core.WrongTypeException;
import org.freshmarker.core.environment.FilterVariableEnvironment;
import org.freshmarker.core.environment.ListEnvironment;
import org.freshmarker.core.environment.ReducingLoopVariableEnvironment;
import org.freshmarker.core.model.TemplateListSequence;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.model.TemplateSequence;
import org.freshmarker.core.model.TemplateSequenceLooper;

import java.util.List;

public class SequenceListFragment extends AbstractListFragment<Object> {

    private final String identifier;

    public SequenceListFragment(TemplateObject list, String identifier, String looperIdentifier, Fragment block, ListInstruction ftl, TemplateObject filter, TemplateObject offset, TemplateObject limit) {
        super(list, looperIdentifier, block, ftl, filter, offset, limit);
        this.identifier = identifier;
    }

    @Override
    public void process(ProcessContext context) {
        try {
            TemplateSequence<Object> evaluate = list.evaluate(context, TemplateSequence.class);
            List<Object> objectList = filterSequence(context, evaluate.sequence());
            TemplateSequenceLooper looper = new TemplateSequenceLooper(objectList);
            processLoop(context, new ListEnvironment(context.getEnvironment(), identifier, looperIdentifier, looper));
        } catch (RuntimeException e) {
            throw new ProcessException(e.getMessage(), ftl, e);
        }
    }

    @Override
    protected void addFilterVariable(FilterVariableEnvironment environment, Object value) {
        environment.setValue(identifier, value);
    }

    @Override
    public Fragment reduce(ReduceContext context) {
        Environment environment = context.getEnvironment();
        try {
            if (context.getFeatureSet().isDisabled(ReductionFeature.UNROLL_LIST)) {
                return simpleReduce(context, environment);
            }
            int unfoldLimit = getUnfoldLimit(context);
            List<Object> objectList = getList(context);
            if (objectList.isEmpty() || objectList.size() > unfoldLimit) {
                return simpleReduce(context, environment);
            }
            TemplateSequenceLooper looper = new TemplateSequenceLooper(objectList);
            SequenceListStrategy strategy = new SequenceListStrategy(identifier, list);
            return reduceLoop(context, new ListEnvironment(context.getEnvironment(), identifier, looperIdentifier, looper), strategy);
        } catch (WrongTypeException e) {
            throw new ReduceException(e.getMessage(), ftl, e);
        } catch (ProcessException e) {
            return this;
        } finally {
            context.setEnvironment(environment);
        }
    }

    private Fragment simpleReduce(ReduceContext context, Environment environment) {
        context.setEnvironment(new ReducingLoopVariableEnvironment(environment, identifier, looperIdentifier));
        Fragment reduce = block.reduce(context);
        return optimize(block, reduce, r -> new SequenceListFragment(list, identifier, looperIdentifier, r, ftl, filter, offset, limit));
    }

    private List<Object> getList(ReduceContext context) {
        TemplateObject templateObject = list.evaluateToObject(context);
        if (templateObject instanceof TemplateListSequence sequence) {
            return filterSequence(context, sequence.sequence());
        }
        return List.of();
    }

    @Override
    public <R> R accept(TemplateVisitor<R> visitor) {
        return visitor.visit(this, identifier, list, looperIdentifier, block, filter, offset, limit);
    }
}
