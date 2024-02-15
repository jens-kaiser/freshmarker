package org.freshmarker.core.environment;

import java.io.Writer;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.freshmarker.core.Environment;
import org.freshmarker.core.ProcessException;
import org.freshmarker.core.UnsupportedDataTypeException;
import org.freshmarker.core.directive.TemplateFunction;
import org.freshmarker.core.directive.UserDirective;
import org.freshmarker.core.formatter.Formatter;
import org.freshmarker.core.model.TemplateNull;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.output.OutputFormat;
import org.freshmarker.core.providers.TemplateObjectProvider;

public class BaseEnvironment implements Environment {

    private final Map<String, Object> dataModel;
    private final Locale locale;
    private final List<TemplateObjectProvider> providers;
    private final OutputFormat outputFormat;
    private final Map<String, UserDirective> userDirectives;
    private final Map<String, TemplateFunction> functions;
    private final Writer writer;
    private final Map<Class<? extends TemplateObject>, Formatter> formatter;

    public BaseEnvironment(Map<String, Object> dataModel, List<TemplateObjectProvider> providers, Locale locale,
                           OutputFormat outputFormat, Map<String, UserDirective> userDirectives,
                           Map<String, TemplateFunction> functions, Writer writer, Map<Class<? extends TemplateObject>, Formatter> formatter) {
        this.dataModel = dataModel;
        this.locale = locale;
        this.outputFormat = outputFormat;
        this.providers = providers;
        this.userDirectives = userDirectives;
        this.functions = functions;
        this.writer = writer;
        this.formatter = formatter;
    }

    @Override
    public TemplateObject mapObject(Object object) {
        return wrap(object);
    }

    @Override
    public TemplateObject getValue(String name) {
        return wrap(dataModel.get(name));
    }

    private TemplateObject wrap(Object o) {
        if (o == null) {
            return TemplateNull.NULL;
        }
        if (o instanceof TemplateObject templateObject) {
            return templateObject;
        }
        Object current;
        if (o instanceof TemplateObjectSupplier<?> templateObject) {
            current = templateObject.get();
        } else {
            current = o;
        }
        return providers.stream().map(p -> p.provide(this, current)).filter(Objects::nonNull)
                .findFirst().orElseThrow(() -> new UnsupportedDataTypeException("unsupported data type: " + o.getClass()));
    }

    @Override
    public Locale getLocale() {
        return locale;
    }

    public OutputFormat getOutputFormat() {
        return outputFormat;
    }

    @Override
    public UserDirective getDirective(String name) {
        return Optional.ofNullable(userDirectives.get(name))
                .orElseThrow(() -> new ProcessException("unknown directive: " + name));
    }

    @Override
    public TemplateFunction getFunction(String name) {
        return Optional.ofNullable(functions.get(name))
                .orElseThrow(() -> new ProcessException("unknown function: " + name));
    }

    @Override
    public Writer getWriter() {
        return writer;
    }

    public <T extends TemplateObject> Formatter getFormatter(Class<T> type) {
        return formatter.getOrDefault(type, (o, l) -> o.toString());
    }
}
