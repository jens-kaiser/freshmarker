package org.freshmarker.core.model;

import java.util.Map;

import org.freshmarker.core.ProcessContext;

public class TemplateBean implements TemplateMap {

    private final Map<String, Object> map;
    private final Class<?> type;

    public TemplateBean(Map<String, Object> map) {
        this(map, null);
    }

    public TemplateBean(Map<String, Object> map, Class<?> type) {
        this.map = map;
        this.type = type;
    }

    public TemplateObject get(ProcessContext context, String name) {
        Object result = map.get(name);
        return result == null ? TemplateNull.NULL : context.getEnvironment().mapObject(result);
    }

    @Override
    public Map<String, Object> map() {
        return map;
    }

    @Override
    public Class<?> getModelType() {
        return type;
    }

    @Override
    public TemplateBean evaluateToObject(ProcessContext context) {
        return this;
    }
}
