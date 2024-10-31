package org.freshmarker.core.environment;

import org.freshmarker.core.Environment;
import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.model.TemplateObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class FilterVariableEnvironment extends WrapperEnvironment {
    private final ProcessContext context;
    private final Map<String, TemplateObject> dataModel = new HashMap<>();

    public FilterVariableEnvironment(Environment contextEnvironment, ProcessContext context) {
        super(contextEnvironment);
        this.context = context;
    }

    public void setValue(String key, Object value) {
        dataModel.put(key, context.mapObject(value));
    }

    @Override
    public TemplateObject getValue(String name) {
        return Objects.requireNonNullElseGet(dataModel.get(name), () -> wrapped.getValue(name));
    }
}
