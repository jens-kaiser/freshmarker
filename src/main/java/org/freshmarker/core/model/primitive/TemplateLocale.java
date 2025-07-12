package org.freshmarker.core.model.primitive;

import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.ProcessException;
import org.freshmarker.core.model.DotHashAddressable;
import org.freshmarker.core.model.TemplateObject;

import java.util.Locale;

public class TemplateLocale extends TemplatePrimitive<Locale> implements DotHashAddressable {
    public TemplateLocale(Locale value) {
        super(value);
    }

    public TemplateLocale(String value) {
        super(createLocale(value));
    }

    private static Locale createLocale(String value) {
        String[] parts = value.split("_");
        return switch (parts.length) {
            case 1 -> Locale.of(parts[0]);
            case 2 -> Locale.of(parts[0], parts[1]);
            default -> Locale.of(parts[0], parts[0], parts[0]);
        };
    }

    @Override
    public String toString() {
        return getValue().toString();
    }

    public TemplateObject get(ProcessContext context, String name) {
        return new TemplateString(switch (name) {
            case "country" -> getValue().getCountry();
            case "language" -> getValue().getLanguage();
            case "country_name" -> getValue().getDisplayCountry(context.getLocale());
            case "language_name" -> getValue().getDisplayLanguage(context.getLocale());
            default -> throw new ProcessException("unknown attribute: " + name);
        });
    }
}
