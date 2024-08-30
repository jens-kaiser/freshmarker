package org.freshmarker.core.fragment;

import ftl.ast.ListInstruction;
import org.freshmarker.core.Environment;
import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.ProcessException;
import org.freshmarker.core.ReduceContext;
import org.freshmarker.core.environment.ListEnvironment;
import org.freshmarker.core.environment.ReducingLoopVariableEnvironment;
import org.freshmarker.core.environment.ReducingVariableEnvironment;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.model.TemplateSequence;
import org.freshmarker.core.model.TemplateSequenceLooper;

import java.util.List;

public class SequenceListFragment extends AbstractListFragment<Object> {

    private final String identifier;

    public SequenceListFragment(TemplateObject list, String identifier, String looperIdentifier, Fragment block, ListInstruction ftl) {
        super(list, looperIdentifier, block, ftl);
        this.identifier = identifier;
    }

    @Override
    public void process(ProcessContext context) {
        try {
            List<Object> objectList = list.evaluate(context, TemplateSequence.class).getSequence(context);
            TemplateSequenceLooper looper = new TemplateSequenceLooper(objectList);
            processLoop(context, new ListEnvironment(context.getEnvironment(), identifier, looperIdentifier, looper));
        } catch (RuntimeException e) {
            throw new ProcessException(e.getMessage(), ftl, e);
        }
    }

    @Override
    public Fragment reduce(ReduceContext context) {
        Environment environment = context.getEnvironment();
        try {
            context.setEnvironment(new ReducingVariableEnvironment(new ReducingLoopVariableEnvironment(environment, identifier, looperIdentifier)));
            Fragment reduce = block.reduce(context);
            return optimize(block, reduce, r -> new SequenceListFragment(list, identifier, looperIdentifier, r, ftl));
        } finally {
            context.setEnvironment(environment);
        }
    }
}
