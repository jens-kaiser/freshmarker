package org.freshmarker.core.environment;

import org.freshmarker.core.BuiltInVariableProvider;
import org.freshmarker.core.Environment;
import org.freshmarker.core.ProcessException;
import org.freshmarker.core.fragment.Fragment;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.providers.TemplateObjectMapper;

import java.time.Clock;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class BaseEnvironment implements Environment {

    private final Map<String, Object> dataModel;
    private final Map<String, TemplateObject> cached;
    private final BuiltInVariableProvider builtInVariableProviders;
    private final Clock clock;
    private final TemplateObjectMapper templateObjectMapper;

    public BaseEnvironment(Map<String, Object> dataModel, BuiltInVariableProvider builtInVariableProviders, Clock clock, TemplateObjectMapper templateObjectMapper) {
        this.dataModel = dataModel;
        this.builtInVariableProviders = builtInVariableProviders;
        this.clock = clock;
        this.templateObjectMapper = templateObjectMapper;
        cached = HashMap.newHashMap(dataModel.size());
    }

    @Override
    public TemplateObject getValue(String name) {
        return cached.computeIfAbsent(name, n -> templateObjectMapper.mapObject(dataModel.get(n)));
    }

    @Override
    public boolean checkVariable(String name) {
        return false;
    }

    @Override
    public Optional<Fragment> getNestedContent() {
        return Optional.empty();
    }

    @Override
    public void createVariable(String name, TemplateObject value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setVariable(String name, TemplateObject value) {
        throw new ProcessException("variable " + name + " not found");
    }

    @Override
    public TemplateObject getVariable(String name) {
        throw new ProcessException("variable " + name + " not found");
    }

    public Clock getClock() {
        return clock;
    }

    public BuiltInVariableProvider getBuiltInVariableProviders() {
        return builtInVariableProviders;
    }
}
