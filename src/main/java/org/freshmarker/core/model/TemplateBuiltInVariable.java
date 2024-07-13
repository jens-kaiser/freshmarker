package org.freshmarker.core.model;

import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.model.primitive.TemplateString;
import org.freshmarker.core.model.temporal.TemplateLocalDateTime;

import java.time.LocalDateTime;

public record TemplateBuiltInVariable(String name) implements TemplateExpression {

    @Override
    public TemplateObject evaluateToObject(ProcessContext context) {
        return switch (name) {
            case "now" -> new TemplateLocalDateTime(LocalDateTime.now()).at(context);
            case "locale" -> new TemplateString(context.getEnvironment().getLocale().toString());
            case "country" -> new TemplateString(context.getEnvironment().getLocale().getCountry());
            case "lang" -> new TemplateString(context.getEnvironment().getLocale().getLanguage());
            case "version" -> new TemplateString(getClass().getPackage().getImplementationVersion());
            default -> throw new IllegalStateException("Unexpected value: " + name);
        };
    }
}
