package org.freshmarker.core.environment;

import org.freshmarker.core.Environment;
import org.freshmarker.core.fragment.Fragment;
import org.freshmarker.core.model.TemplateObject;

import java.util.Optional;

public abstract class WrapperEnvironment implements Environment {

    protected final Environment wrapped;

    protected WrapperEnvironment(Environment wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public TemplateObject getValue(String name) {
        return wrapped.getValue(name);
    }

    @Override
    public Optional<Fragment> getNestedContent() {
        return wrapped.getNestedContent();
    }

    @Override
    public void createVariable(String name, TemplateObject value) {
        wrapped.createVariable(name, value);
    }

    @Override
    public void setVariable(String name, TemplateObject value) {
        wrapped.setVariable(name, value);
    }

    @Override
    public TemplateObject getVariable(String name) {
        return wrapped.getVariable(name);
    }

    @Override
    public boolean checkVariable(String name) {
        return wrapped.checkVariable(name);
    }
}
