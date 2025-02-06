package org.freshmarker.core.fragment;

import ftl.ast.ListInstruction;
import org.freshmarker.core.Environment;
import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.ProcessException;
import org.freshmarker.core.ReduceContext;
import org.freshmarker.core.environment.FilterVariableEnvironment;
import org.freshmarker.core.environment.ListEnvironment;
import org.freshmarker.core.environment.ReducingLoopVariableEnvironment;
import org.freshmarker.core.environment.ReducingVariableEnvironment;
import org.freshmarker.core.model.TemplateHashLooper;
import org.freshmarker.core.model.TemplateMap;
import org.freshmarker.core.model.TemplateObject;

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
            Map<String, Object> map = ((TemplateMap) list.evaluateToObject(context)).map();
            List<Entry<String, Object>> sequence = new ArrayList<>(map.entrySet());
            if (comparator != null) {
                sequence.sort(Entry.comparingByKey(comparator));
            }
            sequence = filterSequence(context, sequence);

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

    @Override
    public Fragment reduce(ReduceContext context) {
        Environment environment = context.getEnvironment();
        try {
            Environment reducingVariableEnvironment = new ReducingVariableEnvironment(new ReducingLoopVariableEnvironment(environment, keyIdentifier, valueIdentifier, looperIdentifier));
            context.setEnvironment(reducingVariableEnvironment);
            Fragment reduce = block.reduce(context);
            return optimize(block, reduce, r -> new HashListFragment(list, keyIdentifier, valueIdentifier, looperIdentifier, r, ftl, comparator, filter, limit, limit));
        } finally {
            context.setEnvironment(environment);
        }
    }

    @Override
    public void accept(TemplateVisitor visitor) {
        visitor.visit(this, keyIdentifier, valueIdentifier, comparator, list, looperIdentifier, block, filter, offset, limit);
    }
}
