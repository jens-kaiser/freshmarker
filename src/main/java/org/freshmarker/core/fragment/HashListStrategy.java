package org.freshmarker.core.fragment;

import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.model.TemplateDynamicKey;
import org.freshmarker.core.model.TemplateHash;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.model.primitive.TemplateNumber;
import org.freshmarker.core.model.primitive.TemplateString;

import java.util.List;

public class HashListStrategy implements ReductionStrategy {
    private final String keyIdentifier;
    private final String valueIdentifier;
    private final HashListFragment fragment;

    public HashListStrategy(String keyIdentifier, String valueIdentifier, HashListFragment fragment) {
        this.keyIdentifier = keyIdentifier;
        this.valueIdentifier = valueIdentifier;
        this.fragment = fragment;
    }

    private record HashKeyAccess(TemplateDynamicKey dynamicKey) implements TemplateObject {

        @Override
        public TemplateObject evaluateToObject(ProcessContext context) {
            TemplateHash hash = dynamicKey.evaluate(context, TemplateHash.class);
            return new TemplateString(hash.entry().getKey());
        }
    }

    private record HashValueAccess(TemplateDynamicKey dynamicKey) implements TemplateObject {

        @Override
        public TemplateObject evaluateToObject(ProcessContext context) {
            TemplateHash hash = dynamicKey.evaluate(context, TemplateHash.class);
            return context.mapObject(hash.entry().getValue());
        }
    }

    @Override
    public void handle(List<Fragment> fragments, int index) {
        if (fragments.stream().allMatch(this::needsVariableContext)) {
            TemplateNumber templateNumber = TemplateNumber.of(index);
            TemplateDynamicKey dynamicKey = new TemplateDynamicKey(fragment.getMapAccess(), templateNumber);
            fragments.addFirst(new VarVariableFragment(keyIdentifier, new HashKeyAccess(dynamicKey), null));
            fragments.addFirst(new VarVariableFragment(valueIdentifier, new HashValueAccess(dynamicKey), null));
        }
    }
}