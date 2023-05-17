package org.freshmarker.core.environment;

import org.freshmarker.core.Environment;
import org.freshmarker.core.model.TemplateHashLoopVariable;
import org.freshmarker.core.model.TemplateHashLooper;
import org.freshmarker.core.model.TemplateObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class HashEnvironment extends WrapperEnvironment {
    private final Map<String, TemplateObject> map = new HashMap<>();

    public HashEnvironment(Environment wrapped, String keyIdentifier, String valueIdentifier, String looperIdentifier, TemplateHashLooper looper) {
        super(wrapped);
        TemplateHashLoopVariable keyLoopVariable = new TemplateHashLoopVariable(looper, true);
        TemplateHashLoopVariable valueLoopVariable = new TemplateHashLoopVariable(looper, false);
        if (looperIdentifier != null) {
            map.put(looperIdentifier, looper);
        }
        map.put(keyIdentifier, keyLoopVariable);
        map.put(valueIdentifier, valueLoopVariable);
    }

    @Override
    public TemplateObject getValue(String name) {
        return Optional.ofNullable(map.get(name)).orElseGet(() -> wrapped.getValue(name));
    }
}
