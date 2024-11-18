package org.freshmarker.core.environment;

import org.freshmarker.core.Environment;
import org.freshmarker.core.ProcessException;
import org.freshmarker.core.UnsupportedDataTypeException;
import org.freshmarker.core.fragment.Fragment;
import org.freshmarker.core.model.TemplateNull;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.providers.TemplateObjectMapper;
import org.freshmarker.core.providers.TemplateObjectProvider;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class BaseEnvironment implements Environment , TemplateObjectMapper {

    private final Map<String, Object> dataModel;
    private final Map<String, TemplateObject> cached;
    private final List<TemplateObjectProvider> providers;
    private final Set<Class<?>> checks = new HashSet<>();

    public BaseEnvironment(Map<String, Object> dataModel, List<TemplateObjectProvider> providers) {
        this.dataModel = dataModel;
        this.providers = providers;
        cached = HashMap.newHashMap(dataModel.size());
    }

    @Override
    public TemplateObject mapObject(Object object) {
        return wrap(object);
    }

    @Override
    public TemplateObject getValue(String name) {
        return cached.computeIfAbsent(name, n -> wrap(dataModel.get(n)));
    }

    @Override
    public boolean checkVariable(String name) {
        return false;
    }

    private TemplateObject wrap(Object o) {
        if (o == null) {
            return TemplateNull.NULL;
        }
        if (o instanceof TemplateObject templateObject) {
            return templateObject;
        }
        Object current = o instanceof TemplateObjectSupplier<?> templateObject ? templateObject.get() : o;
        for (TemplateObjectProvider provider : providers) {
            TemplateObject object = provider.provide(this, current);
            if (object != null) {
                return object;
            }
        }
        throw new UnsupportedDataTypeException("unsupported data type: " + o.getClass());
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
        throw new ProcessException("variable " + name + "not found");
    }

    @Override
    public TemplateObject getVariable(String name) {
        throw new ProcessException("variable " + name + " not found");
    }

    @Override
    public Set<Class<?>> getChecks() {
        return checks;
    }
}
