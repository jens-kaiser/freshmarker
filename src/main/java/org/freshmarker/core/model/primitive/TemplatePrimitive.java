package org.freshmarker.core.model.primitive;

import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.model.TemplateObject;

import java.util.Objects;
import java.util.Optional;

public class TemplatePrimitive<P> implements TemplateObject {

    private final P value;

    public TemplatePrimitive(P value) {
        this.value = Objects.requireNonNull(value);
    }

    @Override
    public boolean isPrimitive() {
        return true;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Optional<TemplatePrimitive<?>> asPrimitive() {
        return Optional.of(this);
    }

    public P getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof TemplatePrimitive<?> templatePrimitive) {
            return value.equals(templatePrimitive.value);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public TemplateObject evaluateToObject(ProcessContext context) {
        return this;
    }

    @Override
    public Class<?> getModelType() {
        return value.getClass();
    }
}
