package org.freshmarker.core.plugin;

import org.freshmarker.api.BuiltIn;
import org.freshmarker.api.extension.BuiltInProvider;
import org.freshmarker.api.extension.Register;
import org.freshmarker.api.extension.support.SingleTypeBuiltInRegister;
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
import java.util.function.UnaryOperator;
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

    private static TemplateString capitalize(TemplateObject value, ProcessContext context) {
        Matcher matcher = CAPITALIZE.matcher(((TemplateString)value).getValue());
        return new TemplateString(matcher.replaceAll(r -> matcher.group(1).toUpperCase(context.getLocale()) + matcher.group(2)));
    }

    private static TemplateString uncapitalize(TemplateObject value, ProcessContext context) {
        Matcher matcher = UNCAPITALIZE.matcher(((TemplateString)value).getValue());
        return new TemplateString(matcher.replaceAll(r -> matcher.group(1).toLowerCase(context.getLocale()) + matcher.group(2)));
    }

    private static TemplateString camelCase(TemplateObject value, ProcessContext context) {
        Locale locale = context.getLocale();
        Matcher matcher = CAMEL_CASE.matcher(((TemplateString)value).getValue().toLowerCase(locale));
        return new TemplateString(matcher.replaceAll(r -> matcher.group(1).toLowerCase(locale) + matcher.group(2).toUpperCase(locale)));
    }

    private static TemplateString kebabCase(TemplateObject value, ProcessContext context) {
        return new TemplateString(((TemplateString)value).getValue().replaceAll(LOWER_CASE_UPPER_CASES, "$1-$2").toLowerCase(context.getLocale()));
    }

    private static TemplateString snakeCase(TemplateObject value, ProcessContext context) {
        return new TemplateString(((TemplateString)value).getValue().replaceAll(LOWER_CASE_UPPER_CASES, "$1_$2").toLowerCase(context.getLocale()));
    }

    private static TemplateString screamingSnakeCase(TemplateObject value, ProcessContext context) {
        return new TemplateString(((TemplateString)value).getValue().replaceAll(LOWER_CASE_UPPER_CASES, "$1_$2").toUpperCase(context.getLocale()));
    }

    private static TemplateBoolean contains(TemplateObject value, TemplateString contains) {
        return TemplateBoolean.from(((TemplateString)value).getValue().contains(contains.getValue()));
    }

    private static TemplateBoolean endsWith(TemplateObject value, TemplateString endsWith) {
        return TemplateBoolean.from(((TemplateString)value).getValue().endsWith(endsWith.getValue()));
    }

    private static TemplateBoolean startsWith(TemplateObject value, TemplateString endsWith) {
        return TemplateBoolean.from(((TemplateString)value).getValue().startsWith(endsWith.getValue()));
    }

    private static TemplateBoolean toBoolean(TemplateObject value) {
        String input = ((TemplateString)value).getValue();
        TemplateBoolean result = BOOLEAN_MAP.get(input);
        if (result == null) {
            throw new ProcessException("cannot convert string to boolean: " + input);
        }
        return result;
    }

    private static TemplateStringMarkup esc(TemplateObject value, ProcessContext context, List<TemplateObject> parameters) {
        BuiltInHelper.checkParametersLength(parameters, 1);
        TemplateString templateString = parameters.getFirst().evaluate(context, TemplateString.class);
        return new TemplateStringMarkup(((TemplateString)value), context.getOutputFormat(templateString.getValue()));
    }

    private static TemplateString padding(TemplateString value, ProcessContext context, List<TemplateObject> parameters) {
        BuiltInHelper.checkParametersLength(parameters, 1, 2);
        String text = value.getValue();
        int size = parameters.getFirst().evaluate(context, TemplateNumber.class).asInt();
        if (text.length() >= size) {
            return value;
        }
        String paddingPattern = parameters.size() < 2 ? " " : parameters.get(1).evaluate(context, TemplateString.class).getValue();
        if (paddingPattern.isEmpty()) {
            return value;
        }
        String padding = padding(paddingPattern, size);
        int paddingSize = size - text.length();
        int paddingLeftSize = paddingSize / 2 + paddingSize % 2;
        return new TemplateString(padding.substring(0, paddingLeftSize) + text + padding.substring(paddingLeftSize + text.length()));
    }

    private static TemplateString padding(TemplateString value, ProcessContext context, List<TemplateObject> parameters, boolean left) {
        BuiltInHelper.checkParametersLength(parameters, 1, 2);
        String text = value.getValue();
        int size = parameters.getFirst().evaluate(context, TemplateNumber.class).asInt();
        if (text.length() >= size) {
            return value;
        }
        String paddingPattern = parameters.size() < 2 ? " " : parameters.get(1).evaluate(context, TemplateString.class).getValue();
        if (paddingPattern.isEmpty()) {
            return value;
        }
        String padding = padding(paddingPattern, size - text.length());
        return new TemplateString(left ? padding + text : text + padding);
    }

    private static String padding(String paddingPattern, int paddingSize) {
        if (paddingPattern.length() < 2) {
            return paddingPattern.repeat(paddingSize);
        }
        int paddingRest = paddingSize % paddingPattern.length();
        String patternStart = paddingPattern.repeat(paddingSize / paddingPattern.length());
        if (paddingRest == 0) {
            return patternStart;
        }
        return patternStart + paddingPattern.substring(0, paddingRest);
    }

    private TemplateString mask(TemplateString value, ProcessContext context, List<TemplateObject> parameters, boolean full) {
        String string = value.getValue();
        if (string.isEmpty()) {
            return value;
        }
        if (full && parameters.isEmpty()) {
            return new TemplateString("*".repeat(string.length()));
        }
        String maskPattern = "*";
        int firstIndex = 0;
        if (!parameters.isEmpty() && parameters.getFirst().evaluateToObject(context) instanceof TemplateString first) {
            maskPattern = first.getValue().isEmpty() ? "*" : first.getValue();
            firstIndex = 1;
        }
        StringBuilder builder = new StringBuilder(string);
        int unmaskCharacters = parameters.size() > firstIndex? parameters.get(firstIndex).evaluate(context, TemplateNumber.class).asInt() : 0;
        for (int i = 0; i < string.length() - unmaskCharacters; i++) {
            if (full || builder.charAt(i) != ' ') {
                builder.setCharAt(i, maskPattern.charAt(i % maskPattern.length()));
            }
        }
        return new TemplateString(builder.toString());
    }

    @Override
    public Register<Class<? extends TemplateObject>, String, BuiltIn> provideBuiltInRegister() {
        BuiltIn noEscape = (x, y, e) -> new TemplateStringMarkup((TemplateString) x, StandardOutputFormats.NONE);
        SingleTypeBuiltInRegister register = new SingleTypeBuiltInRegister(TemplateString.class);
        register.add("upper_case", (x, y, e) -> upperCase(x, e));
        register.add("lower_case", (x, y, e) -> lowerCase(x, e));
        register.add("capitalize", (x, y, e) -> capitalize(x, e));
        register.add("uncapitalize", (x, y, e) -> uncapitalize(x, e));
        register.add("camel_case", "camelCase", (x1, y1, e1) -> camelCase(x1, e1));
        register.add("kebabCase", "kebab_case", (x1, y1, e1) -> kebabCase(x1, e1));
        register.add("snake_case", "snakeCase", (x1, y1, e1) -> snakeCase(x1, e1));
        register.add("screaming_snake_case", (x, y, e) -> screamingSnakeCase(x, e));
        register.add("trim", (x, y, e) -> apply(x, String::trim));
        register.add("strip", (x, y, e) -> apply(x, String::strip));
        register.add("strip_leading", (x, y, e) -> apply(x, String::stripLeading));
        register.add("strip_trailing", (x, y, e) -> apply(x, String::stripTrailing));
        register.add("contains", (x, y, e) -> contains(x, (TemplateString) y.getFirst()));
        register.add("ends_with", "endsWith", (x3, y3, e3) -> endsWith(x3, (TemplateString) y3.getFirst()));
        register.add("starts_with", "startsWith", (x2, y2, e2) -> startsWith(x2, (TemplateString) y2.getFirst()));
        register.add("boolean", (x, y, e) -> toBoolean(x));
        register.add("length", (x, y, e) -> TemplateNumber.of(((TemplateString) x).getValue().length()));
        register.add("esc", "escape", (x1, y1, e1) -> esc(x1, e1, y1));
        register.add("no_esc", "no_escape", noEscape);
        register.add("noEsc", noEscape);
        register.add("slugify", (x, y, e) -> slugify(x));
        register.add("i18n", (x, y, e) -> i18n((TemplateString) x, e, y));
        register.add("blank_to_null", (x, y, e) -> ((TemplateString) x).getValue().isBlank() ? TemplateNull.NULL : x);
        register.add("empty_to_null", (x, y, e) -> ((TemplateString) x).getValue().isEmpty() ? TemplateNull.NULL : x);
        register.add("trim_to_null", (x, y, e) -> trim2null((TemplateString) x));
        register.add("left_pad", (x, y, e) -> padding((TemplateString) x, e, y, true));
        register.add("right_pad", (x, y, e) -> padding((TemplateString) x, e, y, false));
        register.add("center_pad", (x, y, e) -> padding((TemplateString) x, e, y));
        register.add("mask", (x, y, e) -> mask((TemplateString) x, e, y, false));
        register.add("mask_full", (x, y, e) -> mask((TemplateString) x, e, y, true));
        register.add( "is_string", BuiltInHelper.alwaysTrue());
        return register;
    }

    private TemplateString apply(TemplateObject value, UnaryOperator<String> operator) {
        return new TemplateString(operator.apply(((TemplateString)value).getValue()));
    }
}
