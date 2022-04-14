package org.freshmarker.core.plugin;

import de.schegge.holidays.Holidays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.ProcessException;
import org.freshmarker.core.buildin.BuiltIn;
import org.freshmarker.core.buildin.BuiltInKey;
import org.freshmarker.core.buildin.BuiltInKeyBuilder;
import org.freshmarker.core.buildin.FunctionalBuiltIn;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.model.primitive.TemplateBoolean;
import org.freshmarker.core.model.primitive.TemplateString;
import org.freshmarker.core.model.temporal.TemplateLocalDate;

public class HolidayPluginProvider implements PluginProvider {

  private static final BuiltInKeyBuilder<TemplateLocalDate> DATE_BUILDER = new BuiltInKeyBuilder<>(
      TemplateLocalDate.class);

  @Override
  public void registerBuildIn(Map<BuiltInKey, BuiltIn> builtIns) {
    builtIns.put(DATE_BUILDER.of("get_holiday"),
        new FunctionalBuiltIn((TemplateObject x, List<TemplateObject> y, ProcessContext c) -> {
          Locale locale = checkCountry(c.getEnvironment().getLocale());
          TemplateLocalDate value = x.evaluate(c, TemplateLocalDate.class);
          return new TemplateString(getHolidaysFromStore(c, locale).getHoliday(value.getValue()).orElseGet(() -> getOptionalFallback(y)));
        }));
    builtIns.put(DATE_BUILDER.of("is_holiday"),
        new FunctionalBuiltIn((TemplateObject x, List<TemplateObject> y, ProcessContext c) -> {
          Locale locale = checkCountry(c.getEnvironment().getLocale());
          TemplateLocalDate value = x.evaluate(c, TemplateLocalDate.class);
          return TemplateBoolean.from(getHolidaysFromStore(c, locale).isHoliday(value.getValue()));
        }));
  }

  private Holidays getHolidaysFromStore(ProcessContext c, Locale locale) {
    return (Holidays) c.getStore("holidays").computeIfAbsent(locale, l -> Holidays.in(locale, locale));
  }

  private String getOptionalFallback(List<TemplateObject> y) {
    if (y.isEmpty()) {
      return "";
    }
    if (y.size() > 1) {
      throw new ProcessException("only one optional parameter allowed");
    }
    return y.get(0).asString().orElseThrow(() -> new ProcessException("only string constant allowed")).getValue();
  }

  private Locale checkCountry(Locale locale) {
    if (locale.getCountry() == null) {
      throw new ProcessException("country is missing in locale: " + locale);
    }
    return locale;
  }
}
