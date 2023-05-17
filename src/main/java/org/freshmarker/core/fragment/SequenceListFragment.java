package org.freshmarker.core.fragment;

import ftl.ast.ListInstruction;
import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.ProcessException;
import org.freshmarker.core.environment.ListEnvironment;
import org.freshmarker.core.model.TemplateLoopVariable;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.model.TemplateSequence;
import org.freshmarker.core.model.TemplateSequenceLoopVariable;
import org.freshmarker.core.model.TemplateSequenceLooper;

import java.util.List;

public class SequenceListFragment extends AbstractListFragment<Object> {

    private final String identifier;

    public SequenceListFragment(TemplateObject list, String identifier, String looperIdentifier, BlockFragment block, ListInstruction ftl) {
        super(list, looperIdentifier, block, ftl);
        this.identifier = identifier;
    }

    @Override
    public void process(ProcessContext context) {
        try {
            List<Object> objectList = ((TemplateSequence) list.evaluateToObject(context)).getSequence(context);
            TemplateSequenceLooper looper = new TemplateSequenceLooper(objectList);
            TemplateLoopVariable loopVariable = new TemplateSequenceLoopVariable(looper);
            ListEnvironment wrapped = new ListEnvironment(context.getEnvironment(), identifier, looperIdentifier, looper, loopVariable);
            processLoop(context, looper, wrapped);
        } catch (RuntimeException e) {
            throw new ProcessException(e.getMessage(), ftl, e);
        }
    }
}
