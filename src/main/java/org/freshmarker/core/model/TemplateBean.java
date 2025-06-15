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
        mapped = HashMap.newHashMap(map.size());
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
            TemplateObject value = t.evaluateToObject(context);
            mapped.put(name, value);
            return value;
        }
        TemplateObject result = context.mapObject(object);
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
