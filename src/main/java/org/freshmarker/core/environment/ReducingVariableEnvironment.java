package org.freshmarker.core.environment;

import org.freshmarker.core.Environment;
import org.freshmarker.core.model.TemplateNull;
import org.freshmarker.core.model.TemplateObject;

import java.util.HashSet;
import java.util.Set;

public class ReducingVariableEnvironment extends WrapperEnvironment {
    private final Set<String> identifiers = new HashSet<>();

    public ReducingVariableEnvironment(Environment wrapped) {
        super(wrapped);
    }

    @Override
    public TemplateObject getValue(String name) {
        return identifiers.contains(name) ? TemplateNull.NULL : wrapped.getValue(name);
    }

    @Override
    public void createVariable(String name, TemplateObject value) {
        identifiers.add(name);
    }

    @Override
    public void setVariable(String name, TemplateObject value) {
        if (!identifiers.contains(name)) {
            wrapped.setVariable(name, value);
            return;
        }
        identifiers.add(name);
    }

    @Override
    public boolean checkVariable(String name) {
        return identifiers.contains(name);
    }

    @Override
    public TemplateObject getVariable(String name) {
        return identifiers.contains(name) ? TemplateNull.NULL : wrapped.getVariable(name);
    }
}