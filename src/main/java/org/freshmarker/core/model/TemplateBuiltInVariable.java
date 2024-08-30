package org.freshmarker.core.model;

import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.model.primitive.TemplateLocale;
import org.freshmarker.core.model.primitive.TemplateString;
import org.freshmarker.core.model.primitive.TemplateVersion;
import org.freshmarker.core.model.temporal.TemplateLocalDateTime;

import java.time.LocalDateTime;

public record TemplateBuiltInVariable(String name) implements TemplateExpression {

    @Override
    public TemplateObject evaluateToObject(ProcessContext context) {
        return switch (name) {
            case "now" -> new TemplateLocalDateTime(LocalDateTime.now()).at(context);
            case "locale" -> new TemplateLocale(context.getLocale());
            case "country" -> new TemplateString(context.getLocale().getCountry());
            case "lang", "language" -> new TemplateString(context.getLocale().getLanguage());
            case "version" -> new TemplateVersion(getClass().getPackage().getImplementationVersion());
            default -> throw new IllegalStateException("Unexpected value: " + name);
        };
    }
}
