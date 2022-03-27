package org.freshmarker.core.plugin;

import java.sql.Date;
import java.sql.Time;
import java.util.Map;
import java.util.function.Function;
import org.freshmarker.core.buildin.BuildInComponent;
import org.freshmarker.core.buildin.BuildInFunction;
import org.freshmarker.core.buildin.TypedBuildIn;
import org.freshmarker.core.formatter.ClassicDateFormatter;
import org.freshmarker.core.formatter.ClassicDateTimeFormatter;
import org.freshmarker.core.formatter.ClassicTimeFormatter;
import org.freshmarker.core.formatter.Formatter;
import org.freshmarker.core.model.date.TemplateClassicDateTime;
import org.freshmarker.core.model.date.TemplateClassicDate;
import org.freshmarker.core.model.date.TemplateClassicTime;
import org.freshmarker.core.model.primitive.TemplateObject;
import org.freshmarker.core.model.primitive.TemplateString;

public class DatePluginProvider implements PluginProvider {

  @Override
  public void registerBuildIn(Map<String, BuildInComponent> buildIns) {
    register(buildIns, "date", (x, y, e) -> new TemplateClassicDate(new Date(((TemplateClassicDateTime) x).getValue().getTime())));
    register(buildIns, "time", (x, y, e) -> new TemplateClassicTime(new Time(((TemplateClassicDateTime) x).getValue().getTime())));
    register(buildIns, "c", (x, y, e) -> new TemplateString(String.valueOf(x)));
    getBuildInComponent(buildIns, "date").add(TemplateClassicDate.class, new TypedBuildIn((x, y, e) -> x));
    getBuildInComponent(buildIns, "c").add(TemplateClassicDate.class,
        new TypedBuildIn((x, y, e) -> new TemplateString(String.valueOf(x))));
    getBuildInComponent(buildIns, "time").add(TemplateClassicTime.class, new TypedBuildIn((x, y, e) -> x));
    getBuildInComponent(buildIns, "c").add(TemplateClassicTime.class,
        new TypedBuildIn((x, y, e) -> new TemplateString(String.valueOf(x))));
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

  protected void register(Map<String, BuildInComponent> buildIns, String name, BuildInFunction function) {
    getBuildInComponent(buildIns, name).add(TemplateClassicDateTime.class, new TypedBuildIn(function));
  }

  private BuildInComponent getBuildInComponent(Map<String, BuildInComponent> buildIns, String name) {
    return buildIns.computeIfAbsent(name, k -> new BuildInComponent());
  }
}
