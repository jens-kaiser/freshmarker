package org.freshmarker.core.model.primitive;

import org.freshmarker.core.model.TemplateNull;
import org.freshmarker.core.model.TemplateObject;

import java.util.Locale;
import java.util.Optional;

public class TemplateLocale extends TemplatePrimitive<Locale> {
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

    public TemplateObject getLanguage() {
        String language = getValue().getLanguage();
        return language == null ? TemplateNull.NULL : new TemplateString(language);
    }

    public TemplateObject getCountry() {
        String country = getValue().getCountry();
        return country == null ? TemplateNull.NULL : new TemplateString(country);
    }

    @Override
    public String toString() {
        return getValue().toString();
    }
}
