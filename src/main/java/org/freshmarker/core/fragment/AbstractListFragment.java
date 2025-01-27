package org.freshmarker.core.fragment;

import ftl.ast.ListInstruction;
import org.freshmarker.core.Environment;
import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.environment.FilterVariableEnvironment;
import org.freshmarker.core.environment.ListEnvironment;
import org.freshmarker.core.environment.VariableEnvironment;
import org.freshmarker.core.model.TemplateLooper;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.model.primitive.TemplateBoolean;
import org.freshmarker.core.model.primitive.TemplateNumber;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.UnaryOperator;

public abstract class AbstractListFragment<T> implements Fragment {
    protected final TemplateObject list;
    protected final String looperIdentifier;
    protected final Fragment block;
    protected final ListInstruction ftl;
    protected final TemplateObject filter;
    protected final TemplateObject offset;
    protected final TemplateObject limit;

    protected AbstractListFragment(TemplateObject list, String looperIdentifier, Fragment block, ListInstruction ftl, TemplateObject filter, TemplateObject offset, TemplateObject limit) {
        this.list = list;
        this.looperIdentifier = looperIdentifier;
        this.block = block;
        this.ftl = ftl;
        this.filter = filter;
        this.offset = offset;
        this.limit = limit;
    }

    protected abstract void addFilterVariable(FilterVariableEnvironment environment, T value);

    protected List<T> filterSequence(ProcessContext context, List<T> sequence) {
        int intOffset = offset == null ? 0 : offset.evaluate(context, TemplateNumber.class).asInt();
        Integer intLimit = limit == null ? null : limit.evaluate(context, TemplateNumber.class).asInt();
        if (filter != null) {
            sequence = handleFilter(context, sequence, intOffset, intLimit);
        } else if (intLimit != null) {
            sequence = subList(sequence, intOffset, intOffset + intLimit);
        } else if (intOffset > 0) {
            sequence = subList(sequence, intOffset, sequence.size());
        }
        return sequence;
    }

    private List<T> subList(List<T> list, int start, int end) {
        if (start < 0 || start >= end || end > list.size()) {
            return List.of();
        }
        return list.subList(start, end);
    }
    protected List<T> handleFilter(ProcessContext context, List<T> objectList, int offset, Integer intLimit) {
        Environment contextEnvironment = context.getEnvironment();
        int counter = Objects.requireNonNullElse(intLimit, objectList.size());
        try {
            FilterVariableEnvironment filterVariableEnvironment = new FilterVariableEnvironment(contextEnvironment, context);
            context.setEnvironment(filterVariableEnvironment);
            List<T> newList = new ArrayList<>();
            int index = 0;
            for (T value : objectList) {
                if (newList.size() >= counter) {
                    break;
                }
                addFilterVariable(filterVariableEnvironment, value);
                if (filter.evaluate(context, TemplateBoolean.class) == TemplateBoolean.TRUE) {
                    if (index >= offset) {
                        newList.add(value);
                    }
                    index++;
                }
            }
            return newList;
        } finally {
            context.setEnvironment(contextEnvironment);
        }
    }

    protected void processLoop(ProcessContext context, ListEnvironment hashEnvironment) {
        TemplateLooper looper = hashEnvironment.getLooper();
        for (int i = 0, n = looper.size(); i < n; i++) {
            Environment environment = context.getEnvironment();
            try {
                context.setEnvironment(new VariableEnvironment(hashEnvironment));
                block.process(context);
            } finally {
                context.setEnvironment(environment);
            }
            looper.increment();
        }
    }

    protected Fragment optimize(Fragment original, Fragment reduced, UnaryOperator<Fragment> function) {
        if (reduced == ConstantFragment.EMPTY)  {
            return ConstantFragment.EMPTY;
        }
        return original == reduced ? this : function.apply(reduced);
    }

    @Override
    public int getSize() {
        return block.getSize() + 1;
    }
}
