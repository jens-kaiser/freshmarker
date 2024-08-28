package org.freshmarker.core.model;

import org.freshmarker.core.environment.BaseEnvironment;
import org.freshmarker.core.model.TemplateBeanGetterProvider.Getter;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

class BaseGetterMap extends AbstractMap<String, Object> {

    private final Map<String, Getter> getters;
    private final BaseEnvironment environment;
    private final Object bean;

    public BaseGetterMap(Map<String, Getter> getters, BaseEnvironment environment, Object bean) {
        this.getters = getters;
        this.environment = environment;
        this.bean = bean;
    }

    @Override
    public Object get(Object key) {
        Getter m = getters.get(key);
        return m == null ? null : getTemplateObject(m);
    }

    @Override
    public Set<Entry<String, Object>> entrySet() {
        return new AbstractSet<>() {
            @Override
            public int size() {
                return getters.size();
            }

            @Override
            public Iterator<Entry<String, Object>> iterator() {
                return new Iterator<>() {
                    private final Iterator<Entry<String, Getter>> iter = getters.entrySet().iterator();

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException("remove() is not supported");
                    }

                    @Override
                    public Entry<String, Object> next() {
                        final Entry<String, Getter> e = iter.next();
                        final Getter m = e.getValue();

                        return new Entry<>() {
                            @Override
                            public String getKey() {
                                return e.getKey();
                            }

                            @Override
                            public Object getValue() {
                                return getTemplateObject(m);
                            }

                            @Override
                            public Object setValue(Object value) {
                                throw new UnsupportedOperationException("setValue() is not supported");
                            }
                        };
                    }

                    @Override
                    public boolean hasNext() {
                        return iter.hasNext();
                    }
                };
            }
        };
    }

    private TemplateObject getTemplateObject(Getter m) {
        return environment.mapObject(m.get(bean));
    }
}
