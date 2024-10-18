package org.freshmarker.core.model.temporal;

import org.freshmarker.core.model.primitive.TemplatePrimitive;

import java.time.Instant;

public class TemplateInstant extends TemplatePrimitive<Instant> implements TemplateDateTime {
    public TemplateInstant(Instant value) {
        super(value);
    }
}
