package org.freshmarker.core.fragment;

import ftl.ast.ListInstruction;
import org.freshmarker.core.Environment;
import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.environment.VariableEnvironment;
import org.freshmarker.core.model.AbstractTemplateLooper;
import org.freshmarker.core.model.TemplateObject;

public abstract class AbstractListFragment<T> implements Fragment {
    protected final TemplateObject list;
    protected final String looperIdentifier;
    protected final BlockFragment block;
    protected final ListInstruction ftl;

    public AbstractListFragment(TemplateObject list, String looperIdentifier, BlockFragment block, ListInstruction ftl) {
        this.list = list;
        this.looperIdentifier = looperIdentifier;
        this.block = block;
        this.ftl = ftl;
    }

    protected void processLoop(ProcessContext context, AbstractTemplateLooper<T> looper, Environment hashEnvironment) {
        Environment environment = context.getEnvironment();
        context.setEnvironment(new VariableEnvironment(hashEnvironment));
        try {
            for (int i = 0; i < looper.size(); i++) {
                block.process(context);
                looper.increment();
            }
        } finally {
            context.setEnvironment(environment);
        }
    }
}
