package org.freshmarker.core.fragment;

import ftl.ast.ListInstruction;
import org.freshmarker.core.Environment;
import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.environment.ListEnvironment;
import org.freshmarker.core.environment.VariableEnvironment;
import org.freshmarker.core.model.TemplateLooper;
import org.freshmarker.core.model.TemplateObject;

import java.util.function.Function;

public abstract class AbstractListFragment<T> implements Fragment {
    protected final TemplateObject list;
    protected final String looperIdentifier;
    protected final Fragment block;
    protected final ListInstruction ftl;

    protected AbstractListFragment(TemplateObject list, String looperIdentifier, Fragment block, ListInstruction ftl) {
        this.list = list;
        this.looperIdentifier = looperIdentifier;
        this.block = block;
        this.ftl = ftl;
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

    protected Fragment optimize(Fragment original, Fragment reduced, Function<Fragment, Fragment> function) {
        if (reduced == ConstantFragment.EMPTY)  {
            return reduced;
        }
        return original == reduced ? this : function.apply(reduced);
    }

    @Override
    public int getSize() {
        return block.getSize() + 1;
    }
}
