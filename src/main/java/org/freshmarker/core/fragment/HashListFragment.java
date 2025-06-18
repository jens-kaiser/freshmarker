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
import org.freshmarker.core.environment.ReducingVariableEnvironment;
import org.freshmarker.core.model.TemplateHashLooper;
import org.freshmarker.core.model.TemplateMap;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.model.TemplateSequence;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class HashListFragment extends AbstractListFragment<Entry<String, Object>> {

    private final String keyIdentifier;
    private final String valueIdentifier;
    private final Comparator<String> comparator;

    public HashListFragment(TemplateObject list, String keyIdentifier, String valueIdentifier, String looperIdentifier, Fragment block, ListInstruction ftl, Comparator<String> comparator, TemplateObject filter, TemplateObject offset, TemplateObject limit) {
        super(list, looperIdentifier, block, ftl, filter, offset, limit);
        this.keyIdentifier = keyIdentifier;
        this.valueIdentifier = valueIdentifier;
        this.comparator = comparator;
    }

    @Override
    public void process(ProcessContext context) {
        try {
            List<Entry<String, Object>> sequence = getList(context);

            TemplateHashLooper looper = new TemplateHashLooper(sequence);
            ListEnvironment hashEnvironment = new ListEnvironment(context.getEnvironment(), keyIdentifier, valueIdentifier, looperIdentifier, looper);
            processLoop(context, hashEnvironment);
        } catch (RuntimeException e) {
            throw new ProcessException(e.getMessage(), ftl, e);
        }
    }

    @Override
    protected void addFilterVariable(FilterVariableEnvironment environment, Entry<String, Object> value) {
        environment.setValue(keyIdentifier, value.getKey());
        environment.setValue(valueIdentifier, value.getValue());
    }

    private Fragment simpleReduce(ReduceContext context, Environment environment) {
        context.setEnvironment(new ReducingVariableEnvironment(new ReducingLoopVariableEnvironment(environment, keyIdentifier, valueIdentifier, looperIdentifier)));
        Fragment reduce = block.reduce(context);
        return optimize(block, reduce, r -> new HashListFragment(list, keyIdentifier, valueIdentifier, looperIdentifier, r, ftl, comparator, filter, limit, limit));
    }

    @Override
    public Fragment reduce(ReduceContext context) {
        Environment environment = context.getEnvironment();
        try {
            if (context.getFeatureSet().isDisabled(ReductionFeature.UNROLL_LIST)) {
                return simpleReduce(context, environment);
            }
            List<Entry<String, Object>> objectList = getList(context);
            int unfoldLimit = getUnfoldLimit(context);
            if (objectList.isEmpty() || objectList.size() > unfoldLimit) {
                return simpleReduce(context, environment);
            }
            TemplateHashLooper looper = new TemplateHashLooper(objectList);
            HashListStrategy strategy = new HashListStrategy(keyIdentifier, valueIdentifier, this);
            return reduceLoop(context, new ListEnvironment(context.getEnvironment(), keyIdentifier, valueIdentifier, looperIdentifier, looper), strategy);
        } catch (WrongTypeException e) {
            throw new ReduceException(e.getMessage(), e);
        } catch (ProcessException e) {
           return this;
        } finally {
            context.setEnvironment(environment);
        }
    }

    private List<Entry<String, Object>> getList(ProcessContext context) {
        Map<String, Object> map = ((TemplateMap) list.evaluateToObject(context)).map();
        List<Entry<String, Object>> sequence = new ArrayList<>(map.entrySet());
        if (comparator != null) {
            sequence.sort(Entry.comparingByKey(comparator));
        }
        return filterSequence(context, sequence);
    }

    private record HashSequence(List<Entry<String, Object>> sequence) implements TemplateSequence<Entry<String, Object>> {

        @Override
        public TemplateObject evaluateToObject(ProcessContext context) {
            return this;
        }

        @Override
        public int size(ProcessContext context) {
            return sequence.size();
        }
    }

    public TemplateObject getMapAccess() {
        return context -> new HashSequence(getList(context));
    }

    @Override
    public <R> R accept(TemplateVisitor<R> visitor) {
        return visitor.visit(this, keyIdentifier, valueIdentifier, comparator, list, looperIdentifier, block, filter, offset, limit);
    }
}
