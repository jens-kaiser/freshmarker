package org.freshmarker.core.plugin;

import java.util.Formatter;
import java.util.Map;

import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.buildin.BuiltInKey;
import org.freshmarker.core.buildin.BuiltIn;
import org.freshmarker.core.buildin.BuiltInMethod;
import org.freshmarker.core.model.primitive.TemplateNumber;
import org.freshmarker.core.model.primitive.TemplateString;

public class NumberPluginProvider implements PluginProvider {

  @Override
  public void registerBuildIn(Map<BuiltInKey, BuiltIn> builtIns) {
    new MethodBuiltInHelper().registerBuiltIns(this, builtIns);
  }

  @BuiltInMethod("c")
  public static TemplateString computerBuiltIn(TemplateNumber value) {
    return new TemplateString(String.valueOf(value));
  }

  @BuiltInMethod
  public static TemplateNumber abs(TemplateNumber value) {
    return value.abs();
  }

  @BuiltInMethod
  public static TemplateNumber sign(TemplateNumber value) {
    return value.sign();
  }

  @BuiltInMethod
  public static TemplateString format(TemplateNumber value, ProcessContext context, TemplateString format) {
    Formatter formatter = new Formatter(context.getEnvironment().getLocale());
    return new TemplateString(formatter.format(format.getValue(), value.getValue().getNumber()).toString());
  }

  @BuiltInMethod("int")
  public static TemplateNumber castInt(TemplateNumber value) {
    return value.getValue().getType() == TemplateNumber.Type.INTEGER ? value : new TemplateNumber(value.getValue().getNumber().intValue());
  }

  @BuiltInMethod("long")
  public static TemplateNumber castLong(TemplateNumber value) {
    return value.getValue().getType() == TemplateNumber.Type.LONG ? value : new TemplateNumber(value.getValue().getNumber().longValue());
  }

  @BuiltInMethod("short")
  public static TemplateNumber castShort(TemplateNumber value) {
    return value.getValue().getType() == TemplateNumber.Type.SHORT ? value : new TemplateNumber(value.getValue().getNumber().shortValue());
  }

  @BuiltInMethod("byte")
  public static TemplateNumber castByte(TemplateNumber value) {
    return value.getValue().getType() == TemplateNumber.Type.BYTE ? value : new TemplateNumber(value.getValue().getNumber().byteValue());
  }

  @BuiltInMethod("double")
  public static TemplateNumber castDouble(TemplateNumber value) {
    return value.getValue().getType() == TemplateNumber.Type.DOUBLE ? value : new TemplateNumber(value.getValue().getNumber().doubleValue());
  }

  @BuiltInMethod("float")
  public static TemplateNumber castFloat(TemplateNumber value) {
    return value.getValue().getType() == TemplateNumber.Type.FLOAT ? value : new TemplateNumber(value.getValue().getNumber().floatValue());
  }
}
