package org.freshmarker.core.fragment;

import org.freshmarker.core.Environment;
import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.environment.ListEnvironment;
import org.freshmarker.core.environment.VariableEnvironment;
import org.freshmarker.core.model.TemplateLoopVariable;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.model.TemplateSequence;
import org.freshmarker.core.model.TemplateSequenceLoopVariable;
import org.freshmarker.core.model.TemplateSequenceLooper;

import java.util.List;

public class ListFragment implements Fragment {

    private final TemplateObject list;
    private final String identifier;
    private final String looperIdentifier;
    private final BlockFragment block;

    public ListFragment(TemplateObject list, String identifier, String looperIdentifier, BlockFragment block) {
        this.list = list;
        this.identifier = identifier;
        this.looperIdentifier = looperIdentifier;
        this.block = block;
    }

    @Override
    public void process(ProcessContext context) {
        List<Object> objectList = ((TemplateSequence) list.evaluateToObject(context)).getSequence(context);
        TemplateSequenceLooper looper = new TemplateSequenceLooper(objectList, objectList.size());
        TemplateLoopVariable loopVariable = new TemplateSequenceLoopVariable(looper);
        Environment environment = context.getEnvironment();
        Environment listEnvironment = new VariableEnvironment(new ListEnvironment(context.getEnvironment(), identifier, looperIdentifier, looper, loopVariable));
        context.setEnvironment(listEnvironment);
        try {
            for (int i = 0; i < objectList.size(); i++) {
                block.process(context);
                looper.increment();
            }
        } finally {
            context.setEnvironment(environment);
        }
    }
}
