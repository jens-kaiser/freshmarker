package org.freshmarker.core;

import org.freshmarker.core.buildin.BuiltIn;
import org.freshmarker.core.buildin.BuiltInKey;
import org.freshmarker.core.environment.BaseEnvironment;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.output.OutputFormat;
import org.freshmarker.core.output.UndefinedOutputFormat;

import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

public class ProcessContext {

    private Environment environment;
    private final BaseEnvironment baseEnvironment;
    protected final Map<Object, Map<Object, Object>> stores = new HashMap<>();
    protected final Map<BuiltInKey, BuiltIn> builtIns;
    protected final Map<String, OutputFormat> outputs;
    protected Writer writer;

    public ProcessContext(BaseEnvironment baseEnvironment, Environment environment, Map<BuiltInKey, BuiltIn> builtIns, Map<String, OutputFormat> outputs) {
        this.baseEnvironment = baseEnvironment;
        this.environment = environment;
        this.builtIns = builtIns;
        this.outputs = outputs;
        this.writer = environment.getWriter();
    }

    public Environment getEnvironment() {
        return environment;
    }

    public BaseEnvironment getBaseEnvironment() {
        return baseEnvironment;
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

    public BuiltIn getBuiltIn(Class<? extends TemplateObject> type, String name) {
        BuiltIn result = builtIns.get(new BuiltInKey(type, name));
        if (result == null) {
            throw new UnsupportedBuiltInException("unsupported builtin '" + name + "' for " + type.getSimpleName());
        }
        return result;
    }

    public OutputFormat getOutputFormat(String name) {
        return outputs.getOrDefault(name, UndefinedOutputFormat.INSTANCE);
    }

    public boolean reductionCheck(TemplateObject templateObject) {
        return !templateObject.isNull();
    }
}
