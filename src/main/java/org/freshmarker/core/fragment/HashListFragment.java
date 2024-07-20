package org.freshmarker.core.fragment;

import ftl.ast.ListInstruction;
import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.ProcessException;
import org.freshmarker.core.environment.ListEnvironment;
import org.freshmarker.core.model.TemplateHashLooper;
import org.freshmarker.core.model.TemplateMap;
import org.freshmarker.core.model.TemplateObject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;import java.util.TreeMap;

public class HashListFragment extends AbstractListFragment<Entry<String, Object>> {

    private final String keyIdentifier;
    private final String valueIdentifier;
    private final Comparator<String> comparator;

    public HashListFragment(TemplateObject list, String keyIdentifier, String valueIdentifier, String looperIdentifier, BlockFragment block, ListInstruction ftl, Comparator<String> comparator) {
        super(list, looperIdentifier, block, ftl);
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
            TemplateHashLooper looper = new TemplateHashLooper(sequence);
            ListEnvironment hashEnvironment = new ListEnvironment(context.getEnvironment(), keyIdentifier, valueIdentifier, looperIdentifier, looper);
            processLoop(context, hashEnvironment);
        } catch (RuntimeException e) {
            throw new ProcessException(e.getMessage(), ftl, e);
        }
    }
}
