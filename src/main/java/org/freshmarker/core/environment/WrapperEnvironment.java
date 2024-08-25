package org.freshmarker.core.environment;

import org.freshmarker.core.Environment;
import org.freshmarker.core.directive.UserDirective;
import org.freshmarker.core.formatter.Formatter;
import org.freshmarker.core.fragment.Fragment;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.output.OutputFormat;

import java.time.ZoneId;
import java.util.Locale;
import java.util.Optional;

public abstract class WrapperEnvironment implements Environment {

    protected final Environment wrapped;

    protected WrapperEnvironment(Environment wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public Locale getLocale() {
        return wrapped.getLocale();
    }

    @Override
    public ZoneId getZoneId() {
        return wrapped.getZoneId();
    }

    @Override
    public OutputFormat getOutputFormat() {
        return wrapped.getOutputFormat();
    }

    @Override
    public UserDirective getDirective(String nameSpace, String name) {
        return wrapped.getDirective(nameSpace, name);
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

    @Override
    public <T extends TemplateObject> Formatter getFormatter(Class<T> type) {
        return wrapped.getFormatter(type);
    }
}
