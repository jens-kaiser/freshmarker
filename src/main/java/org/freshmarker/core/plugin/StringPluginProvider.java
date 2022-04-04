package org.freshmarker.core.plugin;

import java.util.Map;
import org.freshmarker.core.Environment;
import org.freshmarker.core.buildin.BuiltInKey;
import org.freshmarker.core.buildin.BuiltIn;
import org.freshmarker.core.buildin.BuiltInMethod;
import org.freshmarker.core.model.TemplateMarkup;
import org.freshmarker.core.model.primitive.TemplateBoolean;
import org.freshmarker.core.model.primitive.TemplateNumber;
import org.freshmarker.core.model.primitive.TemplateString;
import org.freshmarker.core.output.NoEscapeFormat;

public class StringPluginProvider implements PluginProvider {

  private static final Map<String, TemplateBoolean> BOOLEAN_MAP = Map.of("true", TemplateBoolean.TRUE, "false",
      TemplateBoolean.FALSE);

  @Override
  public void registerBuildIn(Map<BuiltInKey, BuiltIn> builtIns) {
    new MethodBuiltInHelper().registerBuiltIns(this, builtIns);
  }

  @BuiltInMethod
  public static TemplateString upperCase(TemplateString value, Environment environment) {
    return new TemplateString(value.getValue().toUpperCase(environment.getLocale()));
  }

  @BuiltInMethod
  public static TemplateString lowerCase(TemplateString value, Environment environment) {
    return new TemplateString(value.getValue().toLowerCase(environment.getLocale()));
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
    return new TemplateMarkup(value, NoEscapeFormat.INSTANCE);
  }

  @BuiltInMethod
  public static TemplateMarkup esc(TemplateString value, Environment environment) {
    return new TemplateMarkup(value, environment.getOutputFormat());
  }
}
