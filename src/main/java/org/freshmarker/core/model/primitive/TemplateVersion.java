package org.freshmarker.core.model.primitive;

import org.freshmarker.core.model.version.Version;

import java.util.Optional;

public class TemplateVersion extends TemplatePrimitive<Version> {
    public TemplateVersion(String value) {
        super(Version.byString(value));
    }

    public TemplateNumber major() {
        return new TemplateNumber(getValue().major());
    }

    public TemplateNumber minor() {
        return new TemplateNumber(getValue().minor());
    }

    public TemplateNumber patch() {
        return new TemplateNumber(getValue().patch());
    }

    public TemplateBoolean isBefore(TemplateVersion value) {
        return TemplateBoolean.from(getValue().isBefore(value.getValue()));
    }

    public TemplateBoolean isEqual(TemplateVersion value) {
        return TemplateBoolean.from(getValue().equals(value.getValue()));
    }

    public TemplateBoolean isAfter(TemplateVersion value) {
        return TemplateBoolean.from(getValue().isAfter(value.getValue()));
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
