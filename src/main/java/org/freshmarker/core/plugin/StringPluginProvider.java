package org.freshmarker.core.plugin;

import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.buildin.BuiltIn;
import org.freshmarker.core.buildin.BuiltInKey;
import org.freshmarker.core.buildin.BuiltInMethod;
import org.freshmarker.core.model.TemplateMarkup;
import org.freshmarker.core.model.primitive.TemplateBoolean;
import org.freshmarker.core.model.primitive.TemplateNumber;
import org.freshmarker.core.model.primitive.TemplateString;
import org.freshmarker.core.output.OutputFormat;
import org.freshmarker.core.output.UndefinedOutputFormat;

import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringPluginProvider implements PluginProvider {

    private static final Map<String, TemplateBoolean> BOOLEAN_MAP = Map.of("true", TemplateBoolean.TRUE, "false", TemplateBoolean.FALSE);

    @Override
    public void registerBuildIn(Map<BuiltInKey, BuiltIn> builtIns) {
        new MethodBuiltInHelper().registerBuiltIns(this, builtIns);
    }

    @BuiltInMethod
    public static TemplateString upperCase(TemplateString value, ProcessContext context) {
        return new TemplateString(value.getValue().toUpperCase(context.getEnvironment().getLocale()));
    }

    @BuiltInMethod
    public static TemplateString lowerCase(TemplateString value, ProcessContext context) {
        return new TemplateString(value.getValue().toLowerCase(context.getEnvironment().getLocale()));
    }

    @BuiltInMethod
    public static TemplateString capitalize(TemplateString value, ProcessContext context) {
        Matcher matcher = Pattern.compile("\\b(\\p{javaLowerCase})(\\p{IsAlphabetic}*)\\b").matcher(value.getValue());
        return new TemplateString(matcher.replaceAll(r -> matcher.group(1).toUpperCase(context.getEnvironment().getLocale()) + matcher.group(2)));
    }

    @BuiltInMethod
    public static TemplateString camelCase(TemplateString value, ProcessContext context) {
        Locale locale = context.getEnvironment().getLocale();
        Matcher matcher = Pattern.compile("(\\p{javaLowerCase}+)[_-](\\p{javaLowerCase})").matcher(value.getValue().toLowerCase(locale));
        return new TemplateString(matcher.replaceAll(r -> matcher.group(1).toLowerCase(locale) + matcher.group(2).toUpperCase(locale)));
    }

    @BuiltInMethod
    public static TemplateString kebabCase(TemplateString value, ProcessContext context) {
        return new TemplateString(value.getValue().replaceAll("(\\p{javaLowerCase})(\\p{javaUpperCase}+)", "$1-$2").toLowerCase(context.getEnvironment().getLocale()));
    }

    @BuiltInMethod
    public static TemplateString snakeCase(TemplateString value, ProcessContext context) {
        return new TemplateString(value.getValue().replaceAll("(\\p{javaLowerCase})(\\p{javaUpperCase}+)", "$1_$2").toLowerCase(context.getEnvironment().getLocale()));
    }

    @BuiltInMethod
    public static TemplateString screamingSnakeCase(TemplateString value, ProcessContext context) {
        return new TemplateString(value.getValue().replaceAll("(\\p{javaLowerCase})(\\p{javaUpperCase}+)", "$1_$2").toUpperCase(context.getEnvironment().getLocale()));
    }

    @BuiltInMethod
    public static TemplateString trim(TemplateString value) {
        return new TemplateString(value.getValue().trim());
    }

    @BuiltInMethod
    public static TemplateBoolean contains(TemplateString value, TemplateString contains) {
        return value.getValue().contains(contains.getValue()) ? TemplateBoolean.TRUE : TemplateBoolean.FALSE;
    }

    @BuiltInMethod
    public static TemplateBoolean endsWith(TemplateString value, TemplateString endsWith) {
        return value.getValue().endsWith(endsWith.getValue()) ? TemplateBoolean.TRUE : TemplateBoolean.FALSE;
    }

    @BuiltInMethod("boolean")
    public static TemplateBoolean toBoolean(TemplateString value) {
        String input = value.getValue();
        TemplateBoolean result = BOOLEAN_MAP.get(input);
        if (result == null) {
            throw new IllegalArgumentException("cannot convert string to boolean: " + input);
        }
        return result;
    }

    @BuiltInMethod
    public static TemplateNumber length(TemplateString value) {
        return new TemplateNumber(value.getValue().length());
    }

    @BuiltInMethod
    public static TemplateMarkup noEsc(TemplateString value) {
        return new TemplateMarkup(value, UndefinedOutputFormat.INSTANCE);
    }

    @BuiltInMethod
    public static TemplateMarkup esc(TemplateString value, ProcessContext context, TemplateString parameter) {
        OutputFormat outputFormat = parameter.asString().map(String::valueOf).map(context::getOutputFormat)
                .orElse(context.getEnvironment().getOutputFormat());
        return new TemplateMarkup(value, outputFormat);
    }
}
