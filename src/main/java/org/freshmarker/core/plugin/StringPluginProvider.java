package org.freshmarker.core.plugin;

import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.ProcessException;
import org.freshmarker.core.buildin.BuiltIn;
import org.freshmarker.core.buildin.BuiltInKey;
import org.freshmarker.core.buildin.BuiltInKeyBuilder;
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
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class StringPluginProvider implements PluginProvider {

    private static final Map<String, TemplateBoolean> BOOLEAN_MAP = Map.of("true", TemplateBoolean.TRUE, "false", TemplateBoolean.FALSE);

    private static final BuiltInKeyBuilder<TemplateString> BUILDER = new BuiltInKeyBuilder<>(TemplateString.class);

    private static final String LOWER_CASE_UPPER_CASES = "(\\p{javaLowerCase})(\\p{javaUpperCase}+)";

    private static final Pattern CAPITALIZE = Pattern.compile("\\b(\\p{javaLowerCase})(\\p{IsAlphabetic}*)\\b");
    private static final Pattern UNCAPITALIZE = Pattern.compile("\\b(\\p{javaUpperCase})(\\p{IsAlphabetic}*)\\b");
    private static final Pattern CAMEL_CASE = Pattern.compile("(\\p{javaLowerCase}+)[_-](\\p{javaLowerCase})");

    @Override
    public void registerMapper(Map<Class<?>, Function<Object, TemplateObject>> mapper) {
        mapper.put(String.class, o -> new TemplateString((String) o));
    }

    @Override
    public void registerBuildIn(Map<BuiltInKey, BuiltIn> builtIns) {
        BuiltIn camelCase = (x, y, e) -> camelCase((TemplateString) x, e);
        BuiltIn kebabCase = (x, y, e) -> kebabCase((TemplateString) x, e);
        BuiltIn snakeCase = (x, y, e) -> snakeCase((TemplateString) x, e);
        BuiltIn escape = (x, y, e) -> esc((TemplateString) x, e, y);
        BuiltIn noEscape = (x, y, e) -> new TemplateStringMarkup((TemplateString) x, StandardOutputFormats.NONE);
        BuiltIn startsWith = (x, y, e) -> startsWith((TemplateString) x, (TemplateString) y.getFirst());
        BuiltIn endsWith = (x, y, e) -> endsWith((TemplateString) x, (TemplateString) y.getFirst());
        builtIns.put(BUILDER.of("upper_case"), (x, y, e) -> upperCase((TemplateString) x, e));
        builtIns.put(BUILDER.of("lower_case"), (x, y, e) -> lowerCase((TemplateString) x, e));
        builtIns.put(BUILDER.of("capitalize"), (x, y, e) -> capitalize((TemplateString) x, e));
        builtIns.put(BUILDER.of("uncapitalize"), (x, y, e) -> uncapitalize((TemplateString) x, e));
        builtIns.put(BUILDER.of("camel_case"), camelCase);
        builtIns.put(BUILDER.of("camelCase"), camelCase);
        builtIns.put(BUILDER.of("kebabCase"), kebabCase);
        builtIns.put(BUILDER.of("kebab_case"), kebabCase);
        builtIns.put(BUILDER.of("snake_case"), snakeCase);
        builtIns.put(BUILDER.of("snakeCase"), snakeCase);
        builtIns.put(BUILDER.of("screaming_snake_case"), (x, y, e) -> screamingSnakeCase((TemplateString) x, e));
        builtIns.put(BUILDER.of("trim"), (x, y, e) -> new TemplateString(((TemplateString) x).getValue().trim()));
        builtIns.put(BUILDER.of("contains"), (x, y, e) -> contains((TemplateString) x, (TemplateString) y.getFirst()));
        builtIns.put(BUILDER.of("ends_with"), endsWith);
        builtIns.put(BUILDER.of("endsWith"), endsWith);
        builtIns.put(BUILDER.of("starts_with"), startsWith);
        builtIns.put(BUILDER.of("startsWith"), startsWith);
        builtIns.put(BUILDER.of("boolean"), (x, y, e) -> toBoolean((TemplateString) x));
        builtIns.put(BUILDER.of("length"), (x, y, e) -> TemplateNumber.of(((TemplateString) x).getValue().length()));
        builtIns.put(BUILDER.of("esc"), escape);
        builtIns.put(BUILDER.of("escape"), escape);
        builtIns.put(BUILDER.of("no_esc"), noEscape);
        builtIns.put(BUILDER.of("no_escape"), noEscape);
        builtIns.put(BUILDER.of("noEsc"), noEscape);
        builtIns.put(BUILDER.of("slugify"), (x, y, e) -> slugify((TemplateString) x));
        builtIns.put(BUILDER.of("i18n"), (x, y, e) -> i18n((TemplateString) x, e, y));
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

    private static TemplateString slugify(TemplateString x) {
        return new TemplateString(x.getValue().replaceAll("[^ a-zA-Z0-9-]", "").replace(' ', '-').toLowerCase());
    }

    private static TemplateString upperCase(TemplateString value, ProcessContext context) {
        return new TemplateString(value.getValue().toUpperCase(context.getLocale()));
    }

    private static TemplateString lowerCase(TemplateString value, ProcessContext context) {
        return new TemplateString(value.getValue().toLowerCase(context.getLocale()));
    }

    public static TemplateString capitalize(TemplateString value, ProcessContext context) {
        Matcher matcher = CAPITALIZE.matcher(value.getValue());
        return new TemplateString(matcher.replaceAll(r -> matcher.group(1).toUpperCase(context.getLocale()) + matcher.group(2)));
    }

    public static TemplateString uncapitalize(TemplateString value, ProcessContext context) {
        Matcher matcher = UNCAPITALIZE.matcher(value.getValue());
        return new TemplateString(matcher.replaceAll(r -> matcher.group(1).toLowerCase(context.getLocale()) + matcher.group(2)));
    }

    public static TemplateString camelCase(TemplateString value, ProcessContext context) {
        Locale locale = context.getLocale();
        Matcher matcher = CAMEL_CASE.matcher(value.getValue().toLowerCase(locale));
        return new TemplateString(matcher.replaceAll(r -> matcher.group(1).toLowerCase(locale) + matcher.group(2).toUpperCase(locale)));
    }

    public static TemplateString kebabCase(TemplateString value, ProcessContext context) {
        return new TemplateString(value.getValue().replaceAll(LOWER_CASE_UPPER_CASES, "$1-$2").toLowerCase(context.getLocale()));
    }

    public static TemplateString snakeCase(TemplateString value, ProcessContext context) {
        return new TemplateString(value.getValue().replaceAll(LOWER_CASE_UPPER_CASES, "$1_$2").toLowerCase(context.getLocale()));
    }

    public static TemplateString screamingSnakeCase(TemplateString value, ProcessContext context) {
        return new TemplateString(value.getValue().replaceAll(LOWER_CASE_UPPER_CASES, "$1_$2").toUpperCase(context.getLocale()));
    }

    public static TemplateBoolean contains(TemplateString value, TemplateString contains) {
        return TemplateBoolean.from(value.getValue().contains(contains.getValue()));
    }

    public static TemplateBoolean endsWith(TemplateString value, TemplateString endsWith) {
        return TemplateBoolean.from(value.getValue().endsWith(endsWith.getValue()));
    }

    public static TemplateBoolean startsWith(TemplateString value, TemplateString endsWith) {
        return TemplateBoolean.from(value.getValue().startsWith(endsWith.getValue()));
    }

    public static TemplateBoolean toBoolean(TemplateString value) {
        String input = value.getValue();
        TemplateBoolean result = BOOLEAN_MAP.get(input);
        if (result == null) {
            throw new ProcessException("cannot convert string to boolean: " + input);
        }
        return result;
    }

    public static TemplateStringMarkup esc(TemplateString value, ProcessContext context, List<TemplateObject> parameters) {
        BuiltInHelper.checkParametersLength(parameters, 1);
        TemplateString templateString = parameters.getFirst().evaluate(context, TemplateString.class);
        return new TemplateStringMarkup(value, context.getOutputFormat(templateString.getValue()));
    }
}
