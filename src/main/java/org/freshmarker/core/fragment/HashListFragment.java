package org.freshmarker.core.fragment;

import ftl.ast.ListInstruction;
import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.ProcessException;
import org.freshmarker.core.environment.ListEnvironment;
import org.freshmarker.core.model.TemplateHashLooper;
import org.freshmarker.core.model.TemplateMap;
import org.freshmarker.core.model.TemplateObject;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class HashListFragment extends AbstractListFragment<Entry<String, Object>> {

    private final String keyIdentifier;
    private final String valueIdentifier;

    public HashListFragment(TemplateObject list, String keyIdentifier, String valueIdentifier, String looperIdentifier, BlockFragment block, ListInstruction ftl) {
        super(list, looperIdentifier, block, ftl);
        this.keyIdentifier = keyIdentifier;
        this.valueIdentifier = valueIdentifier;
    }

    @Override
    public void process(ProcessContext context) {
        try {
            Map<String, Object> map = ((TemplateMap) list.evaluateToObject(context)).map();
            TemplateHashLooper looper = new TemplateHashLooper(List.copyOf(map.entrySet()));
            ListEnvironment hashEnvironment = new ListEnvironment(context.getEnvironment(), keyIdentifier, valueIdentifier, looperIdentifier, looper);
            processLoop(context, looper, hashEnvironment);
        } catch (RuntimeException e) {
            throw new ProcessException(e.getMessage(), ftl, e);
        }
    }
}
