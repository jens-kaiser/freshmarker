package org.freshmarker.core.plugin;

import org.freshmarker.api.extension.BuiltIn;
import org.freshmarker.api.extension.BuiltInProvider;
import org.freshmarker.api.extension.Register;
import org.freshmarker.api.extension.support.BuiltInRegister;
import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.ProcessException;
import org.freshmarker.core.model.TemplateNull;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.model.TemplateStringMarkup;
import org.freshmarker.core.model.primitive.TemplateBoolean;
import org.freshmarker.core.model.primitive.TemplateNumber;
import org.freshmarker.core.model.primitive.TemplateString;
import org.freshmarker.core.output.StandardOutputFormats;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class StringBuiltInProvider implements BuiltInProvider {

    private static final Map<String, TemplateBoolean> BOOLEAN_MAP = Map.of("true", TemplateBoolean.TRUE, "false", TemplateBoolean.FALSE);

    private static final String LOWER_CASE_UPPER_CASES = "(\\p{javaLowerCase})(\\p{javaUpperCase}+)";

    private static final Pattern CAPITALIZE = Pattern.compile("\\b(\\p{javaLowerCase})(\\p{IsAlphabetic}*)\\b");
    private static final Pattern UNCAPITALIZE = Pattern.compile("\\b(\\p{javaUpperCase})(\\p{IsAlphabetic}*)\\b");
    private static final Pattern CAMEL_CASE = Pattern.compile("(\\p{javaLowerCase}+)[_-](\\p{javaLowerCase})");

    private static TemplateObject trim2null(TemplateString x) {
        String value = x.getValue().trim();
        return value.isEmpty() ? TemplateNull.NULL : new TemplateString(value);
    }

    private TemplateObject i18n(TemplateString x, ProcessContext e, List<TemplateObject> y) {
        BuiltInHelper.checkParametersLength(y, 0, 1);
        String resourceBundle = y.isEmpty() ? e.getResourceBundle() : y.getFirst().evaluate(e, TemplateString.class).getValue();
        try {
            return new TemplateString(ResourceBundle.getBundle(resourceBundle, e.getLocale()).getString(x.getValue()));
        } catch (RuntimeException ex) {
            return TemplateNull.NULL;
        }
    }

    private static TemplateString slugify(TemplateObject value) {
        return new TemplateString(((TemplateString)value).getValue().replaceAll("[^ a-zA-Z0-9-]", "").replace(' ', '-').toLowerCase());
    }

    private static TemplateString upperCase(TemplateObject value, ProcessContext context) {
        return new TemplateString(((TemplateString)value).getValue().toUpperCase(context.getLocale()));
    }

    private static TemplateString lowerCase(TemplateObject value, ProcessContext context) {
        return new TemplateString(((TemplateString)value).getValue().toLowerCase(context.getLocale()));
    }

    public static TemplateString capitalize(TemplateObject value, ProcessContext context) {
        Matcher matcher = CAPITALIZE.matcher(((TemplateString)value).getValue());
        return new TemplateString(matcher.replaceAll(r -> matcher.group(1).toUpperCase(context.getLocale()) + matcher.group(2)));
    }

    public static TemplateString uncapitalize(TemplateObject value, ProcessContext context) {
        Matcher matcher = UNCAPITALIZE.matcher(((TemplateString)value).getValue());
        return new TemplateString(matcher.replaceAll(r -> matcher.group(1).toLowerCase(context.getLocale()) + matcher.group(2)));
    }

    public static TemplateString camelCase(TemplateObject value, ProcessContext context) {
        Locale locale = context.getLocale();
        Matcher matcher = CAMEL_CASE.matcher(((TemplateString)value).getValue().toLowerCase(locale));
        return new TemplateString(matcher.replaceAll(r -> matcher.group(1).toLowerCase(locale) + matcher.group(2).toUpperCase(locale)));
    }

    public static TemplateString kebabCase(TemplateObject value, ProcessContext context) {
        return new TemplateString(((TemplateString)value).getValue().replaceAll(LOWER_CASE_UPPER_CASES, "$1-$2").toLowerCase(context.getLocale()));
    }

    public static TemplateString snakeCase(TemplateObject value, ProcessContext context) {
        return new TemplateString(((TemplateString)value).getValue().replaceAll(LOWER_CASE_UPPER_CASES, "$1_$2").toLowerCase(context.getLocale()));
    }

    public static TemplateString screamingSnakeCase(TemplateObject value, ProcessContext context) {
        return new TemplateString(((TemplateString)value).getValue().replaceAll(LOWER_CASE_UPPER_CASES, "$1_$2").toUpperCase(context.getLocale()));
    }

    public static TemplateBoolean contains(TemplateObject value, TemplateString contains) {
        return TemplateBoolean.from(((TemplateString)value).getValue().contains(contains.getValue()));
    }

    public static TemplateBoolean endsWith(TemplateObject value, TemplateString endsWith) {
        return TemplateBoolean.from(((TemplateString)value).getValue().endsWith(endsWith.getValue()));
    }

    public static TemplateBoolean startsWith(TemplateObject value, TemplateString endsWith) {
        return TemplateBoolean.from(((TemplateString)value).getValue().startsWith(endsWith.getValue()));
    }

    public static TemplateBoolean toBoolean(TemplateObject value) {
        String input = ((TemplateString)value).getValue();
        TemplateBoolean result = BOOLEAN_MAP.get(input);
        if (result == null) {
            throw new ProcessException("cannot convert string to boolean: " + input);
        }
        return result;
    }

    public static TemplateStringMarkup esc(TemplateObject value, ProcessContext context, List<TemplateObject> parameters) {
        BuiltInHelper.checkParametersLength(parameters, 1);
        TemplateString templateString = parameters.getFirst().evaluate(context, TemplateString.class);
        return new TemplateStringMarkup(((TemplateString)value), context.getOutputFormat(templateString.getValue()));
    }

    @Override
    public Register<Class<? extends TemplateObject>, String, BuiltIn> provideBuiltInRegister() {
        BuiltIn camelCase = (x, y, e) -> camelCase(x, e);
        BuiltIn kebabCase = (x, y, e) -> kebabCase(x, e);
        BuiltIn snakeCase = (x, y, e) -> snakeCase(x, e);
        BuiltIn escape = (x, y, e) -> esc(x, e, y);
        BuiltIn noEscape = (x, y, e) -> new TemplateStringMarkup((TemplateString) x, StandardOutputFormats.NONE);
        BuiltIn startsWith = (x, y, e) -> startsWith(x, (TemplateString) y.getFirst());
        BuiltIn endsWith = (x, y, e) -> endsWith(x, (TemplateString) y.getFirst());
        BuiltInRegister register = new BuiltInRegister();
        register.add(TemplateString.class, "upper_case", (x, y, e) -> upperCase(x, e));
        register.add(TemplateString.class, "lower_case", (x, y, e) -> lowerCase(x, e));
        register.add(TemplateString.class, "capitalize", (x, y, e) -> capitalize(x, e));
        register.add(TemplateString.class, "uncapitalize", (x, y, e) -> uncapitalize(x, e));
        register.add(TemplateString.class, "camel_case", camelCase);
        register.add(TemplateString.class, "camelCase", camelCase);
        register.add(TemplateString.class, "kebabCase", kebabCase);
        register.add(TemplateString.class, "kebab_case", kebabCase);
        register.add(TemplateString.class, "snake_case", snakeCase);
        register.add(TemplateString.class, "snakeCase", snakeCase);
        register.add(TemplateString.class, "screaming_snake_case", (x, y, e) -> screamingSnakeCase(x, e));
        register.add(TemplateString.class, "trim", (x, y, e) -> new TemplateString(((TemplateString) x).getValue().trim()));
        register.add(TemplateString.class, "contains", (x, y, e) -> contains(x, (TemplateString) y.getFirst()));
        register.add(TemplateString.class, "ends_with", endsWith);
        register.add(TemplateString.class, "endsWith", endsWith);
        register.add(TemplateString.class, "starts_with", startsWith);
        register.add(TemplateString.class, "startsWith", startsWith);
        register.add(TemplateString.class, "boolean", (x, y, e) -> toBoolean(x));
        register.add(TemplateString.class, "length", (x, y, e) -> TemplateNumber.of(((TemplateString) x).getValue().length()));
        register.add(TemplateString.class, "esc", escape);
        register.add(TemplateString.class, "escape", escape);
        register.add(TemplateString.class, "no_esc", noEscape);
        register.add(TemplateString.class, "no_escape", noEscape);
        register.add(TemplateString.class, "noEsc", noEscape);
        register.add(TemplateString.class, "slugify", (x, y, e) -> slugify(x));
        register.add(TemplateString.class, "i18n", (x, y, e) -> i18n((TemplateString) x, e, y));
        register.add(TemplateString.class, "blank_to_null", (x, y, e) -> ((TemplateString) x).getValue().isBlank() ? TemplateNull.NULL : x);
        register.add(TemplateString.class, "empty_to_null", (x, y, e) -> ((TemplateString) x).getValue().isEmpty() ? TemplateNull.NULL : x);
        register.add(TemplateString.class, "trim_to_null", (x, y, e) -> trim2null((TemplateString) x));
        return register;
    }
}
