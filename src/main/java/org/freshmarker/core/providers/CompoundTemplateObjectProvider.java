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

    public interface CompoundHandler {
        TemplateObject provide(Object o);
    }

    private final List<CompoundHandler> handlers = new ArrayList<>();

    @SuppressWarnings("unchecked")
    public CompoundTemplateObjectProvider(boolean setAsSequence, boolean collectionAsSequence) {
        handlers.add(o -> {
            if (o instanceof List<?> list) {
                return new TemplateListSequence((List<Object>) list);
            }
            return null;
        });
        handlers.add(o -> {
            if (o instanceof Map<?,?> map) {
                return new TemplateBean((Map<String, Object>) map, null);
            }
            return null;
        });
        handlers.add(o -> {
            if (o instanceof SequencedCollection<?> sequenced) {
                return new TemplateListSequence(new ArrayList<>(sequenced));
            }
            return null;
        });
        handlers.add(o -> {
            if (o.getClass().isArray()) {
                int length = Array.getLength(o);
                Object[] array = (Object[])Array.newInstance(Object.class, length);
                for (int i = 0; i < length; i++) {
                    array[i] = Array.get(o, i);
                }
                return new TemplateListSequence((List.of(array)));
            }
            return null;
        });
        if (setAsSequence) {
            handlers.add(o -> {
                if (o instanceof Set<?> set) {
                    return new TemplateListSequence(new ArrayList<>(set));
                }
                return null;
            });
        }
        if (collectionAsSequence) {
            handlers.add(o -> {
                if (o instanceof Collection<?> collection) {
                    return new TemplateListSequence(new ArrayList<>(collection));
                }
                return null;
            });
        }
    }

    @Override
    public TemplateObject provide(TemplateObjectMapper environment, Object o) {
        for (CompoundTemplateObjectProvider.CompoundHandler handler : handlers) {
            TemplateObject provided = handler.provide(o);
            if (provided != null) {
                return provided;
            }
        }
        return null;
    }
}
