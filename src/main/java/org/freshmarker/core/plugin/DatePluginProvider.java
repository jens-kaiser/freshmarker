package org.freshmarker.core.plugin;

import java.sql.Date;
import java.sql.Time;
import java.util.Map;
import java.util.function.Function;
import org.freshmarker.core.buildin.BuiltIn;
import org.freshmarker.core.buildin.BuildInKey;
import org.freshmarker.core.buildin.BuiltInKeyBuilder;
import org.freshmarker.core.buildin.TypedBuiltIn;
import org.freshmarker.core.formatter.ClassicDateFormatter;
import org.freshmarker.core.formatter.ClassicDateTimeFormatter;
import org.freshmarker.core.formatter.ClassicTimeFormatter;
import org.freshmarker.core.formatter.Formatter;
import org.freshmarker.core.model.date.TemplateClassicDate;
import org.freshmarker.core.model.date.TemplateClassicDateTime;
import org.freshmarker.core.model.date.TemplateClassicTime;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.model.primitive.TemplateString;

public class DatePluginProvider implements PluginProvider {

  private static final BuiltInKeyBuilder<TemplateClassicDateTime> DATE_TIME_BUILDER = new BuiltInKeyBuilder<>(
      TemplateClassicDateTime.class);

  private static final BuiltInKeyBuilder<TemplateClassicDate> DATE_BUILDER = new BuiltInKeyBuilder<>(
      TemplateClassicDate.class);

  private static final BuiltInKeyBuilder<TemplateClassicTime> TIME_BUILDER = new BuiltInKeyBuilder<>(
      TemplateClassicTime.class);

  @Override
  public void registerBuildIn(Map<BuildInKey, BuiltIn> builtIns) {
    builtIns.put(DATE_TIME_BUILDER.of("date"), new TypedBuiltIn(
        (x3, y3, e3) -> new TemplateClassicDate(new Date(((TemplateClassicDateTime) x3).getValue().getTime()))));
    builtIns.put(DATE_TIME_BUILDER.of("time"), new TypedBuiltIn(
        (x2, y2, e2) -> new TemplateClassicTime(new Time(((TemplateClassicDateTime) x2).getValue().getTime()))));
    builtIns.put(DATE_TIME_BUILDER.of("c"), new TypedBuiltIn((x1, y1, e1) -> new TemplateString(String.valueOf(x1))
    ));
    builtIns.put(DATE_BUILDER.of("date"), new TypedBuiltIn((x, y, e) -> x));
    builtIns.put(DATE_BUILDER.of("c"), new TypedBuiltIn((x, y, e) -> new TemplateString(String.valueOf(x))));
    builtIns.put(TIME_BUILDER.of("time"), new TypedBuiltIn((x, y, e) -> x));
    builtIns.put(TIME_BUILDER.of("c"), new TypedBuiltIn((x, y, e) -> new TemplateString(String.valueOf(x))));
  }

  @Override
  public void registerMapper(Map<Class<?>, Function<Object, TemplateObject>> mapper) {
    mapper.put(java.util.Date.class, o -> new TemplateClassicDateTime((java.util.Date) o));
    mapper.put(Date.class, o -> new TemplateClassicDate((Date) o));
    mapper.put(Time.class, o -> new TemplateClassicTime((Time) o));
  }

  @Override
  public void registerFormatter(Map<Class<? extends TemplateObject>, Formatter> formatter) {
    formatter.put(TemplateClassicDateTime.class, new ClassicDateTimeFormatter("yyyy-MM-dd hh:mm:ss"));
    formatter.put(TemplateClassicDate.class, new ClassicDateFormatter("yyyy-MM-dd"));
    formatter.put(TemplateClassicTime.class, new ClassicTimeFormatter("hh:mm:ss"));
  }
}
