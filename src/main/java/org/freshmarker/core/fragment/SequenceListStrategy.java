package org.freshmarker.core.fragment;

import org.freshmarker.core.model.TemplateDynamicKey;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.model.primitive.TemplateNumber;

import java.util.List;

public class SequenceListStrategy implements ReductionStrategy {
    private final String identifier;
    private final TemplateObject list;

    public SequenceListStrategy(String identifier, TemplateObject list) {
        this.identifier = identifier;
        this.list = list;
    }

    @Override
    public void handle(List<Fragment> fragments, int index) {
        if (fragments.stream().allMatch(this::needsVariableContext)) {
            fragments.addFirst(new VarVariableFragment(identifier, new TemplateDynamicKey(list, TemplateNumber.of(index)), null));
        }
    }
}
