package org.freshmarker.core.environment;

import org.freshmarker.core.Environment;
import org.freshmarker.core.ProcessException;
import org.freshmarker.core.UnsupportedDataTypeException;
import org.freshmarker.core.directive.TemplateFunction;
import org.freshmarker.core.directive.UserDirective;
import org.freshmarker.core.formatter.Formatter;
import org.freshmarker.core.fragment.Fragment;
import org.freshmarker.core.model.TemplateNull;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.output.OutputFormat;
import org.freshmarker.core.providers.TemplateObjectProvider;

import java.io.Writer;
import java.time.ZoneId;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class BaseEnvironment implements Environment {

    private final Map<String, Object> dataModel;
    private final List<TemplateObjectProvider> providers;
    private final Map<NameSpaced, UserDirective> userDirectives;
    private final Map<String, TemplateFunction> functions;
    private final Writer writer;
    private final Settings settings;

    public BaseEnvironment(Map<String, Object> dataModel, List<TemplateObjectProvider> providers,
                           Map<NameSpaced, UserDirective> userDirectives, Map<String, TemplateFunction> functions,
                           Writer writer, Settings settings) {
        this.dataModel = dataModel;
        this.providers = providers;
        this.userDirectives = userDirectives;
        this.functions = functions;
        this.writer = writer;
        this.settings = settings;
    }

    @Override
    public TemplateObject mapObject(Object object) {
        return wrap(object);
    }

    @Override
    public TemplateObject getValue(String name) {
        return wrap(dataModel.get(name));
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
        return providers.stream().map(p -> p.provide(this, current)).filter(Objects::nonNull).findFirst()
                .orElseThrow(() -> new UnsupportedDataTypeException("unsupported data type: " + o.getClass()));
    }

    @Override
    public Locale getLocale() {
        return settings.locale();
    }

    @Override
    public ZoneId getZoneId() {
        return settings.zoneId();
    }

    public OutputFormat getOutputFormat() {
        return settings.format();
    }

    @Override
    public UserDirective getDirective(String nameSpace, String name) {
        return Optional.ofNullable(userDirectives.get(new NameSpaced(nameSpace, name))).orElseThrow(() -> new ProcessException("unknown directive: " + name));
    }

    @Override
    public TemplateFunction getFunction(String name) {
        return Optional.ofNullable(functions.get(name)).orElseThrow(() -> new ProcessException("unknown function: " + name));
    }

    @Override
    public Writer getWriter() {
        return writer;
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
        throw new UnsupportedOperationException();
    }

    @Override
    public TemplateObject getVariable(String name) {
        throw new UnsupportedOperationException();
    }

    public <T extends TemplateObject> Formatter getFormatter(Class<T> type) {
        return settings.formatters().getOrDefault(type, (o, l) -> o.toString());
    }
}
