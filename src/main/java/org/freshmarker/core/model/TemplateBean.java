package org.freshmarker.core.model;

import java.util.HashMap;
import java.util.Map;

import org.freshmarker.core.ProcessContext;

public class TemplateBean implements TemplateMap {

    private final Map<String, Object> map;
    private final Class<?> type;
    private final Map<String, TemplateObject> mapped;

    public TemplateBean(Map<String, Object> map, Class<?> type) {
        this.map = map;
        this.type = type;
        mapped = new HashMap<>(map.size());
    }

    public TemplateObject get(ProcessContext context, String name) {
        TemplateObject templateObject = mapped.get(name);
        if (templateObject != null) {
            return templateObject;
        }
        Object object = map.get(name);
        if (object == null) {
            mapped.put(name, TemplateNull.NULL);
            return TemplateNull.NULL;
        }
        if (object instanceof TemplateObject t) {
            mapped.put(name, t);
            return t;
        }
        TemplateObject result = context.getBaseEnvironment().mapObject(object);
        mapped.put(name, result);
        return result;
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
