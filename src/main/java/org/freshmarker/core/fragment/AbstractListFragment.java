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
    protected final TemplateObject limit;

    protected AbstractListFragment(TemplateObject list, String looperIdentifier, Fragment block, ListInstruction ftl, TemplateObject filter, TemplateObject limit) {
        this.list = list;
        this.looperIdentifier = looperIdentifier;
        this.block = block;
        this.ftl = ftl;
        this.filter = filter;
        this.limit = limit;
    }

    protected abstract void addFilterVariable(FilterVariableEnvironment environment, T value);

    protected List<T> filterSequence(ProcessContext context, List<T> sequence) {
        Integer intLimit = limit == null ? null : limit.evaluate(context, TemplateNumber.class).asInt();
        if (filter != null) {
            sequence = handleFilter(context, sequence, intLimit);
        } else if (intLimit != null) {
            sequence = sequence.subList(0, intLimit);
        }
        return sequence;
    }

    protected List<T> handleFilter(ProcessContext context, List<T> objectList, Integer intLimit) {
        Environment contextEnvironment = context.getEnvironment();
        int counter = Objects.requireNonNullElse(intLimit, objectList.size());
        try {
            FilterVariableEnvironment filterVariableEnvironment = new FilterVariableEnvironment(contextEnvironment, context);
            context.setEnvironment(filterVariableEnvironment);
            List<T> newList = new ArrayList<>();
            for (int i = 0; i < objectList.size(); i++) {
                if (i >= counter) {
                    break;
                }
                T value = objectList.get(i);
                addFilterVariable(filterVariableEnvironment, value);
                if (filter.evaluate(context, TemplateBoolean.class) == TemplateBoolean.TRUE) {
                    newList.add(value);
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
