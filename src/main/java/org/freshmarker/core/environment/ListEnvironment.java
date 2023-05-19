package org.freshmarker.core.environment;

import org.freshmarker.core.Environment;
import org.freshmarker.core.model.TemplateHashLoopVariable;
import org.freshmarker.core.model.TemplateHashLooper;
import org.freshmarker.core.model.TemplateLooper;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.model.TemplateSequenceLoopVariable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ListEnvironment extends WrapperEnvironment {
    private final Map<String, TemplateObject> map = new HashMap<>();
    private final TemplateLooper looper;

    private ListEnvironment(Environment wrapped, String looperIdentifier, TemplateLooper looper) {
        super(wrapped);
        this.looper = looper;
        if (looperIdentifier != null) {
            map.put(looperIdentifier, looper);
        }
    }

    public ListEnvironment(Environment wrapped, String identifier, String looperIdentifier, TemplateLooper looper) {
        this(wrapped, looperIdentifier, looper);
        map.put(identifier, new TemplateSequenceLoopVariable(looper));
    }

    public ListEnvironment(Environment wrapped, String keyIdentifier, String valueIdentifier, String looperIdentifier, TemplateHashLooper looper) {
        this(wrapped, looperIdentifier, looper);
        map.put(keyIdentifier, new TemplateHashLoopVariable(looper, true));
        map.put(valueIdentifier, new TemplateHashLoopVariable(looper, false));
    }

    @Override
    public TemplateObject getValue(String name) {
        return Optional.ofNullable(map.get(name)).orElseGet(() -> wrapped.getValue(name));
    }

    public TemplateLooper getLooper() {
        return looper;
    }
}
