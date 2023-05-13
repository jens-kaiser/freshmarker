package org.freshmarker.core.fragment;

import org.freshmarker.core.Environment;
import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.environment.HashEnvironment;
import org.freshmarker.core.environment.VariableEnvironment;
import org.freshmarker.core.model.TemplateHashLoopVariable;
import org.freshmarker.core.model.TemplateHashLooper;
import org.freshmarker.core.model.TemplateMap;
import org.freshmarker.core.model.TemplateObject;

import java.util.List;
import java.util.Map;

public class HashListFragment implements Fragment {

    private final TemplateObject list;
    private final String keyIdentifier;
    private final String valueIdentifier;
    private final String looperIdentifier;
    private final BlockFragment block;

    public HashListFragment(TemplateObject list, String keyIdentifier, String valueIdentifier, String looperIdentifier, BlockFragment block) {
        this.list = list;
        this.keyIdentifier = keyIdentifier;
        this.valueIdentifier = valueIdentifier;
        this.looperIdentifier = looperIdentifier;
        this.block = block;
    }

    @Override
    public void process(ProcessContext context) {
        Map<String, Object> map = ((TemplateMap) list.evaluateToObject(context)).map();
        TemplateHashLooper looper = new TemplateHashLooper(List.copyOf(map.entrySet()));
        TemplateHashLoopVariable keyLoopVariable = new TemplateHashLoopVariable(looper, true);
        TemplateHashLoopVariable valueLoopVariable = new TemplateHashLoopVariable(looper, false);
        Environment environment = context.getEnvironment();
        Environment listEnvironment = new VariableEnvironment(new HashEnvironment(context.getEnvironment(), keyIdentifier, valueIdentifier, looperIdentifier, looper, keyLoopVariable, valueLoopVariable));
        context.setEnvironment(listEnvironment);
        for (int i = 0; i < map.size(); i++) {
            block.process(context);
            looper.increment();
        }
        context.setEnvironment(environment);
    }
}
