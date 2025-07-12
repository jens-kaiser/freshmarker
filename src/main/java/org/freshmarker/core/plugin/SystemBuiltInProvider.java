package org.freshmarker.core.plugin;

import org.freshmarker.api.extension.BuiltInProvider;
import org.freshmarker.api.extension.Register;
import org.freshmarker.api.extension.support.BuiltInRegister;
import org.freshmarker.core.ProcessContext;
import org.freshmarker.api.BuiltIn;
import org.freshmarker.core.ProcessException;
import org.freshmarker.core.model.TemplateBean;
import org.freshmarker.core.model.TemplateNull;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.model.primitive.TemplateBoolean;
import org.freshmarker.core.model.primitive.TemplateEnum;
import org.freshmarker.core.model.primitive.TemplateLocale;
import org.freshmarker.core.model.primitive.TemplateNumber;
import org.freshmarker.core.model.primitive.TemplateString;
import org.freshmarker.core.model.primitive.TemplateVersion;

import java.util.List;
import java.util.ResourceBundle;

public final class SystemBuiltInProvider implements BuiltInProvider {

    private static final String LANGUAGE = "language";

    private static TemplateVersion version(TemplateObject x, ProcessContext e) {
        return x.evaluate(e, TemplateVersion.class);
    }

    private static TemplateObject locale(TemplateObject x, ProcessContext e, String name) {
        return x.evaluate(e, TemplateLocale.class).get(e, name);
    }

    private static TemplateVersion parameter(List<TemplateObject> objects, ProcessContext context) {
        if (objects.size() != 1) {
            throw new IllegalArgumentException("invalid argument lists: size=" + objects.size());
        }
        TemplateObject value = objects.getFirst().evaluateToObject(context);
        return switch (value) {
            case TemplateVersion version -> version;
            case TemplateString string -> new TemplateVersion(string.toString());
            default -> throw new ProcessException("invalid type: " + value.getModelType());
        };
    }

    private static TemplateObject thenBuildIn(TemplateObject value, List<TemplateObject> parameters, ProcessContext context) {
        BuiltInHelper.checkParametersLength(parameters, 2);
        return value == TemplateBoolean.TRUE ? parameters.getFirst().evaluateToObject(context) : parameters.get(1).evaluateToObject(context);
    }

    private static TemplateString humanBuiltIn(TemplateObject value, ProcessContext context) {
        return new TemplateString(ResourceBundle.getBundle("freshmarker", context.getLocale()).getString("boolean." + value));
    }

    @Override
    public Register<Class<? extends TemplateObject>, String, BuiltIn> provideBuiltInRegister() {
        BuiltInRegister register = new BuiltInRegister();
        register.add(TemplateVersion.class, "is_before", (x, y, e) -> version(x, e).isBefore(parameter(y, e)));
        register.add(TemplateVersion.class, "is_after", (x, y, e) -> version(x, e).isAfter(parameter(y, e)));
        register.add(TemplateVersion.class, "is_equal", (x, y, e) -> version(x, e).isEqual(parameter(y, e)));
        register.add(TemplateVersion.class, "major", (x, y, e) -> version(x, e).get(e, "major"));
        register.add(TemplateVersion.class, "minor", (x, y, e) -> version(x, e).get(e, "minor"));
        register.add(TemplateVersion.class, "patch", (x, y, e) -> version(x, e).get(e, "patch"));
        register.add(TemplateString.class, "version", (x, y, e) -> new TemplateVersion(x.evaluate(e, TemplateString.class).toString()));
        register.add(TemplateString.class, "locale", (x, y, e) -> new TemplateLocale(x.evaluate(e, TemplateString.class).toString()));
        register.add(TemplateLocale.class, "lang", (x, y, e) -> locale(x, e, LANGUAGE));
        register.add(TemplateLocale.class, LANGUAGE, (x, y, e) -> locale(x, e, LANGUAGE));
        register.add(TemplateLocale.class, "language_name", (x, y, e) -> locale(x, e, "language_name"));
        register.add(TemplateLocale.class, "country", (x, y, e) -> locale(x, e, "country"));
        register.add(TemplateLocale.class, "country_name", (x, y, e) -> locale(x, e, "country_name"));
        register.add(TemplateNull.class, "empty_to_null", BuiltIn.identity());
        register.add(TemplateNull.class, "blank_to_null", BuiltIn.identity());
        register.add(TemplateNull.class, "trim_to_null", BuiltIn.identity());
        register.add(TemplateNull.class, "strip_to_null", BuiltIn.identity());
        register.add(TemplateNull.class, "is_null", BuiltInHelper.alwaysTrue());
        register.add(TemplateBoolean.class, "c", BuiltIn.string());
        register.add(TemplateBoolean.class, "then", SystemBuiltInProvider::thenBuildIn);
        register.add(TemplateBoolean.class, "h", (x, y, e) ->  SystemBuiltInProvider.humanBuiltIn(x, e));
        register.add(TemplateBoolean.class, "is_boolean", BuiltInHelper.alwaysTrue());
        register.add(TemplateEnum.class, "c", (x, y, e) -> new TemplateString(((TemplateEnum<?>) x).getValue().name()));
        register.add(TemplateEnum.class, "ordinal", (x, y, e) -> TemplateNumber.of(((TemplateEnum<?>) x).getValue().ordinal()));
        register.add(TemplateEnum.class, "is_enum", BuiltInHelper.alwaysTrue());
        register.add(TemplateBean.class, "is_hash", BuiltInHelper.alwaysTrue());
        return register;
    }
}
