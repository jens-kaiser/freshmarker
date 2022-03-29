package org.freshmarker.core.plugin;

import java.sql.Date;
import java.sql.Time;
import java.util.Map;
import java.util.function.Function;
import org.freshmarker.core.buildin.BuildInKey;
import org.freshmarker.core.buildin.BuildInKeyBuilder;
import org.freshmarker.core.buildin.TypedBuildIn;
import org.freshmarker.core.formatter.ClassicDateFormatter;
import org.freshmarker.core.formatter.ClassicDateTimeFormatter;
import org.freshmarker.core.formatter.ClassicTimeFormatter;
import org.freshmarker.core.formatter.Formatter;
import org.freshmarker.core.model.date.TemplateClassicDate;
import org.freshmarker.core.model.date.TemplateClassicDateTime;
import org.freshmarker.core.model.date.TemplateClassicTime;
import org.freshmarker.core.model.primitive.TemplateObject;
import org.freshmarker.core.model.primitive.TemplateString;

public class DatePluginProvider implements PluginProvider {

  private static final BuildInKeyBuilder<TemplateClassicDateTime> DATE_TIME_BUILDER = new BuildInKeyBuilder<>(
      TemplateClassicDateTime.class);

  private static final BuildInKeyBuilder<TemplateClassicDate> DATE_BUILDER = new BuildInKeyBuilder<>(
      TemplateClassicDate.class);

  private static final BuildInKeyBuilder<TemplateClassicTime> TIME_BUILDER = new BuildInKeyBuilder<>(
      TemplateClassicTime.class);

  @Override
  public void registerBuildIn(Map<BuildInKey, TypedBuildIn> buildIns) {
    buildIns.put(DATE_TIME_BUILDER.of("date"), new TypedBuildIn(
        (x3, y3, e3) -> new TemplateClassicDate(new Date(((TemplateClassicDateTime) x3).getValue().getTime()))));
    buildIns.put(DATE_TIME_BUILDER.of("time"), new TypedBuildIn(
        (x2, y2, e2) -> new TemplateClassicTime(new Time(((TemplateClassicDateTime) x2).getValue().getTime()))));
    buildIns.put(DATE_TIME_BUILDER.of("c"), new TypedBuildIn((x1, y1, e1) -> new TemplateString(String.valueOf(x1))));
    buildIns.put(DATE_BUILDER.of("date"), new TypedBuildIn((x, y, e) -> x));
    buildIns.put(DATE_BUILDER.of("c"), new TypedBuildIn((x, y, e) -> new TemplateString(String.valueOf(x))));
    buildIns.put(TIME_BUILDER.of("time"), new TypedBuildIn((x, y, e) -> x));
    buildIns.put(TIME_BUILDER.of("c"), new TypedBuildIn((x, y, e) -> new TemplateString(String.valueOf(x))));
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
