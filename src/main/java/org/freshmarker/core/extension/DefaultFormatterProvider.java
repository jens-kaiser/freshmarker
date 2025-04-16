package org.freshmarker.core.extension;

import org.freshmarker.api.Formatter;
import org.freshmarker.api.extension.FormatterProvider;
import org.freshmarker.core.formatter.BooleanFormatter;
import org.freshmarker.core.formatter.NumberFormatter;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.model.primitive.TemplateBoolean;
import org.freshmarker.core.model.primitive.TemplateNumber;

import java.util.Map;

public class DefaultFormatterProvider implements FormatterProvider {
    @Override
    public Map<Class<? extends TemplateObject>, Formatter> providerFormatter() {
        return Map.of(TemplateBoolean.class, new BooleanFormatter("yes", "no"), TemplateNumber.class, new NumberFormatter());
    }
}
