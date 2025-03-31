package org.freshmarker.core.plugin;

import org.freshmarker.api.extension.BuiltInProvider;
import org.freshmarker.api.extension.support.MapEntryBuilder;
import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.buildin.BuiltIn;
import org.freshmarker.core.buildin.BuiltInKey;
import org.freshmarker.core.model.TemplateNull;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.model.primitive.TemplateBoolean;
import org.freshmarker.core.model.primitive.TemplateEnum;
import org.freshmarker.core.model.primitive.TemplateLocale;
import org.freshmarker.core.model.primitive.TemplateNumber;
import org.freshmarker.core.model.primitive.TemplateString;
import org.freshmarker.core.model.primitive.TemplateVersion;

import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public final class SystemBuiltInProvider implements BuiltInProvider {

    private static TemplateVersion version(TemplateObject x, ProcessContext e) {
        return x.evaluate(e, TemplateVersion.class);
    }

    private static TemplateLocale locale(TemplateObject x, ProcessContext e) {
        return x.evaluate(e, TemplateLocale.class);
    }

    private static TemplateVersion parameter(List<TemplateObject> objects, ProcessContext context) {
        if (objects.size() != 1) {
            throw new IllegalArgumentException("invalid argument lists: size=" + objects.size());
        }
        TemplateObject value = objects.getFirst().evaluateToObject(context);
        return switch (value) {
            case TemplateVersion version -> version;
            case TemplateString string -> new TemplateVersion(string.toString());
            default -> throw new IllegalStateException("invalid type: " + value.getModelType());
        };
    }

    private static TemplateObject thenBuildIn(TemplateObject value, List<TemplateObject> parameters, ProcessContext context) {
        BuiltInHelper.checkParametersLength(parameters, 2);
        return value == TemplateBoolean.TRUE ? parameters.getFirst().evaluateToObject(context) : parameters.get(1).evaluateToObject(context);
    }

    private static TemplateString stringBuiltIn(TemplateObject value, List<TemplateObject> parameters, ProcessContext context) {
        BuiltInHelper.checkParametersLength(parameters, 2);
        return value == TemplateBoolean.TRUE ? parameters.getFirst().evaluate(context, TemplateString.class) : parameters.get(1).evaluate(context, TemplateString.class);
    }

    private static TemplateString humanBuiltIn(TemplateObject value, List<TemplateObject> parameters, ProcessContext context) {
        return new TemplateString(ResourceBundle.getBundle("freshmarker", context.getLocale()).getString("boolean." + value));
    }

    @Override
    public Map<BuiltInKey, BuiltIn> provideBuiltIns() {
        MapEntryBuilder<TemplateVersion> version = new MapEntryBuilder<>(TemplateVersion.class);
        MapEntryBuilder<TemplateString> string = new MapEntryBuilder<>(TemplateString.class);
        MapEntryBuilder<TemplateLocale> locale = new MapEntryBuilder<>(TemplateLocale.class);
        MapEntryBuilder<TemplateNull> nullBuilder = new MapEntryBuilder<>(TemplateNull.class);
        MapEntryBuilder<TemplateBoolean> booleanBuilder = new MapEntryBuilder<>(TemplateBoolean.class);
        MapEntryBuilder<? extends TemplateObject> enumBuilder = new MapEntryBuilder<>(TemplateEnum.class);

        return Map.ofEntries(
                version.entry("is_before", (x, y, e) -> version(x, e).isBefore(parameter(y, e))),
                version.entry("is_after", (x, y, e) -> version(x, e).isAfter(parameter(y, e))),
                version.entry("is_equal", (x, y, e) -> version(x, e).isEqual(parameter(y, e))),
                version.entry("major", (x, y, e) -> version(x, e).major()),
                version.entry("minor", (x, y, e) -> version(x, e).minor()),
                version.entry("patch", (x, y, e) -> version(x, e).patch()),
                string.entry("version", (x, y, e) -> new TemplateVersion(x.evaluate(e, TemplateString.class).toString())),
                string.entry("locale", (x, y, e) -> new TemplateLocale(x.evaluate(e, TemplateString.class).toString())),
                locale.entry("lang", (x, y, e) -> locale(x, e).getLanguage()),
                locale.entry("language", (x, y, e) -> locale(x, e).getLanguage()),
                locale.entry("language_name", (x, y, e) -> locale(x, e).getDisplayLanguage(e.getLocale())),
                locale.entry("country", (x, y, e) -> locale(x, e).getCountry()),
                locale.entry("country_name", (x, y, e) -> locale(x, e).getDisplayCountry(e.getLocale())),
                nullBuilder.entry("empty_to_null", BuiltIn.identity()),
                nullBuilder.entry("blank_to_null", BuiltIn.identity()),
                nullBuilder.entry("trim_to_null", BuiltIn.identity()),
                booleanBuilder.entry("c", BuiltIn.string()),
                booleanBuilder.entry("then", SystemBuiltInProvider::thenBuildIn),
                booleanBuilder.entry("string", SystemBuiltInProvider::stringBuiltIn),
                booleanBuilder.entry("h", SystemBuiltInProvider::humanBuiltIn),
                enumBuilder.entry("c", (x, y, e) -> new TemplateString(((TemplateEnum<?>) x).getValue().name())),
                enumBuilder.entry("ordinal", (x, y, e) -> TemplateNumber.of(((TemplateEnum<?>) x).getValue().ordinal()))
        );
    }
}