package org.freshmarker.core.model.primitive;

import java.util.Locale;

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

    public TemplateString getLanguage() {
        System.out.println(getValue().getLanguage());
        return new TemplateString(getValue().getLanguage());
    }

    public TemplateString getDisplayLanguage(Locale locale) {
        return new TemplateString(getValue().getDisplayLanguage(locale));
    }

    public TemplateString getCountry() {
        return new TemplateString(getValue().getCountry());
    }

    public TemplateString getDisplayCountry(Locale locale) {
        return new TemplateString(getValue().getDisplayCountry(locale));
    }

    @Override
    public String toString() {
        return getValue().toString();
    }
}
