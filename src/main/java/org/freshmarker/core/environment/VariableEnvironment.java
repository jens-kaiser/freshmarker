package org.freshmarker.core.environment;

import org.freshmarker.core.Environment;
import org.freshmarker.core.model.TemplateObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class VariableEnvironment extends WrapperEnvironment {

    private final Map<String, TemplateObject> dataModel = new HashMap<>();

    public VariableEnvironment(Environment wrapped) {
        super(wrapped);
    }

    @Override
    public void createVariable(String name, TemplateObject value) {
        dataModel.put(name, value);
    }

    @Override
    public void setVariable(String name, TemplateObject value) {
        if (dataModel.get(name) == null) {
            wrapped.setVariable(name, value);
            return;
        }
        dataModel.put(name, value);
    }

    @Override
    public boolean checkVariable(String name) {
        return dataModel.get(name) != null;
    }

    @Override
    public TemplateObject getValue(String name) {
        return Objects.requireNonNullElseGet(dataModel.get(name), () -> wrapped.getValue(name));
    }

    @Override
    public TemplateObject getVariable(String name) {
        return Objects.requireNonNullElseGet(dataModel.get(name), () -> wrapped.getValue(name));
    }
}
