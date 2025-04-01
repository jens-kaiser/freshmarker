package org.freshmarker.core.buildin;

import org.freshmarker.core.model.TemplateObject;

@Deprecated(since = "1.8.0", forRemoval = true)
public class MapEntryBuilder<T extends TemplateObject> extends org.freshmarker.api.extension.support.MapEntryBuilder<T> {

    public MapEntryBuilder(Class<T> type) {
        super(type);
    }
}
