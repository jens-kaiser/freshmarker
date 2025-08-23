package org.freshmarker.core.providers;

import org.freshmarker.core.model.TemplateBean;
import org.freshmarker.core.model.TemplateListSequence;
import org.freshmarker.core.model.TemplateObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.SequencedCollection;
import java.util.Set;

public class CompoundTemplateObjectProvider implements TemplateObjectProvider {

    private final boolean setAsSequence;
    private final boolean collectionAsSequence;

    public CompoundTemplateObjectProvider(boolean setAsSequence, boolean collectionAsSequence) {
        this.setAsSequence = setAsSequence;
        this.collectionAsSequence = collectionAsSequence;
    }

    private static TemplateListSequence provideArray(Object o) {
        int length = Array.getLength(o);
        Object[] array = (Object[])Array.newInstance(Object.class, length);
        for (int i = 0; i < length; i++) {
            array[i] = Array.get(o, i);
        }
        return new TemplateListSequence((List.of(array)));
    }

    @Override
    @SuppressWarnings("unchecked")
    public TemplateObject provide(TemplateObjectMapper environment, Object o) {
        return switch (o) {
            case List<?> list -> new TemplateListSequence((List<Object>) list);
            case Map<?, ?> map -> new TemplateBean((Map<String, Object>) map, null);
            case SequencedCollection<?> sequenced -> new TemplateListSequence(new ArrayList<>(sequenced));
            case Set<?> set when setAsSequence -> new TemplateListSequence(new ArrayList<>(set));
            case Collection<?> collection when collectionAsSequence -> new TemplateListSequence(new ArrayList<>(collection));
            default -> o.getClass().isArray() ? provideArray(o) : null;
        };
    }
}
