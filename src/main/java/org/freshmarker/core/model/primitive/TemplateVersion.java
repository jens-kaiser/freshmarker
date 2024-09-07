package org.freshmarker.core.model.primitive;

import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.model.version.Version;

import java.util.Optional;

public class TemplateVersion extends TemplatePrimitive<Version> {
    public TemplateVersion(String value) {
        super(Version.byString(value));
    }

    public TemplateNumber major() {
        return TemplateNumber.of(getValue().major());
    }

    public TemplateNumber minor() {
        return TemplateNumber.of(getValue().minor());
    }

    public TemplateNumber patch() {
        return TemplateNumber.of(getValue().patch());
    }

    public TemplateBoolean isBefore(TemplateVersion value) {
        return TemplateBoolean.from(getValue().compareTo(value.getValue()) < 0);
    }

    public TemplateBoolean isEqual(TemplateVersion value) {
        return TemplateBoolean.from(getValue().equals(value.getValue()));
    }

    public TemplateBoolean isAfter(TemplateVersion value) {
        return TemplateBoolean.from(getValue().compareTo(value.getValue()) > 0);
    }

    @Override
    public String toString() {
        return getValue().toString();
    }

    @Override
    public Optional<TemplateString> asString() {
        return Optional.of(new TemplateString(toString()));
    }
}
