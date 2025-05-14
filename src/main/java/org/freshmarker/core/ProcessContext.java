package org.freshmarker.core;

import org.freshmarker.api.BuiltIn;
import org.freshmarker.core.buildin.BuiltInKey;
import org.freshmarker.api.Formatter;
import org.freshmarker.api.TemplateFunction;
import org.freshmarker.api.UserDirective;
import org.freshmarker.core.environment.BaseEnvironment;
import org.freshmarker.core.environment.NameSpaced;
import org.freshmarker.core.environment.VariableEnvironment;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.api.OutputFormat;
import org.freshmarker.core.model.primitive.TemplateBoolean;
import org.freshmarker.core.output.StandardOutputFormats;

import java.io.Writer;
import java.time.Clock;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public class ProcessContext {
    private static final Formatter SIMPLE = (object, locale) -> object.toString();

    private static final List<String> TYPE_CHECK_BUILT_INS = List.of("is_null", "is_string", "is_boolean", "is_number");

    private Writer writer;
    protected Environment environment;
    protected final BaseEnvironment baseEnvironment;
    protected final Map<Object, Map<Object, Object>> stores = new HashMap<>();
    protected final Map<BuiltInKey, BuiltIn> builtIns;
    protected final Map<String, OutputFormat> outputs;
    protected final Map<String, TemplateFunction> functions;
    protected final List<Locale> locals = new LinkedList<>();
    protected final List<ZoneId> zoneIds = new LinkedList<>();
    protected final List<OutputFormat> outputFormats = new LinkedList<>();
    protected final List<Map<Class<? extends TemplateObject>, Formatter>> formatters = new LinkedList<>();
    protected final List<Map<NameSpaced, UserDirective>> userDirectives;
    protected String resourceBundleName;

    public ProcessContext(ProcessContext context) {
        this.baseEnvironment = context.baseEnvironment;
        this.environment = context.environment;
        this.builtIns = context.builtIns;
        this.outputs = context.outputs;
        this.writer = context.writer;
        this.functions = context.functions;
        this.userDirectives = context.userDirectives;
        this.locals.addFirst(context.getLocale());
        this.zoneIds.addFirst(context.getZoneId());
        this.formatters.addFirst(context.formatters.getFirst());
        this.outputFormats.addFirst(context.getOutputFormat());
        this.resourceBundleName = context.resourceBundleName;
    }

    public ProcessContext(StaticContext context, BaseEnvironment baseEnvironment, Map<NameSpaced, UserDirective> userDirectives, OutputFormat outputFormat, Locale locale, ZoneId zoneId, Writer writer, Map<Class<? extends TemplateObject>, Formatter> formatter) {
        this.baseEnvironment = baseEnvironment;
        this.environment = new VariableEnvironment(baseEnvironment);
        this.writer = writer;
        this.userDirectives = List.of(userDirectives, context.userDirectives());
        this.builtIns = context.builtIns();
        this.outputs = context.outputs();
        this.functions = context.functions();
        this.formatters.addFirst(formatter);
        this.locals.addFirst(locale);
        this.zoneIds.addFirst(zoneId);
        this.outputFormats.addFirst(outputFormat);
    }

    public Environment getEnvironment() {
        return environment;
    }

    public TemplateObject mapObject(Object object) {
        return baseEnvironment.mapObject(object);
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public void setWriter(Writer writer) {
        this.writer = writer;
    }

    public Writer getWriter() {
        return writer;
    }

    public Map<Object, Object> getStore(Object key) {
        return stores.computeIfAbsent(key, k -> new HashMap<>());
    }

    public TemplateFunction getFunction(String name) {
        return Optional.ofNullable(functions.get(name)).orElseThrow(() -> new ProcessException("unknown function: " + name));
    }

    public BuiltIn getBuiltIn(Class<? extends TemplateObject> type, String name) {
        BuiltIn result = builtIns.get(new BuiltInKey(type, name));
        if (result != null) {
            return result;
        }
        if (TYPE_CHECK_BUILT_INS.contains(name)) {
            return BuiltIn.value(TemplateBoolean.FALSE);
        }
        throw new UnsupportedBuiltInException("unsupported builtin '" + name + "' for " + type.getSimpleName());
    }

    public OutputFormat getOutputFormat(String name) {
        return outputs.getOrDefault(name, StandardOutputFormats.NONE);
    }

    public boolean reductionCheck(TemplateObject templateObject) {
        return !templateObject.isNull();
    }

    public void push(Locale locale) {
        locals.addFirst(locale);
    }

    public Locale getLocale() {
        return locals.getFirst();
    }

    public void push(ZoneId zoneId) {
        zoneIds.addFirst(zoneId);
    }

    public ZoneId getZoneId() {
        return zoneIds.getFirst();
    }

    public void pushOutputFormat(OutputFormat format) {
        outputFormats.addFirst(format);
    }

    public void pullOutputFormat() {
        outputFormats.removeFirst();
    }

    public OutputFormat getOutputFormat() {
        return outputFormats.getFirst();
    }

    public void pushFormatter(Map<Class<? extends TemplateObject>, Formatter> formatter) {
        formatters.addFirst(formatter);
    }

    public <T extends TemplateObject> Formatter getFormatter(Class<T> type) {
        for (Map<Class<? extends TemplateObject>, Formatter> map : formatters) {
            Formatter formatter = map.get(type);
            if (formatter != null) {
                return formatter;
            }
        }
        return SIMPLE;
    }

    public UserDirective getDirective(String nameSpace, String name) {
        for (Map<NameSpaced, UserDirective> map : userDirectives) {
            UserDirective userDirective = map.get(new NameSpaced(nameSpace, name));
            if (userDirective != null) {
                return userDirective;
            }
        }
        throw new ProcessException("unknown directive: " + name);
    }

    public void setResourceBundle(String resourceBundleName) {
        this.resourceBundleName = resourceBundleName;
    }

    public String getResourceBundle() {
        return resourceBundleName;
    }

    public BaseEnvironment getBaseEnvironment() {
        return baseEnvironment;
    }

    public Clock getClock() {
        return baseEnvironment.getClock().withZone(getZoneId());
    }
}
