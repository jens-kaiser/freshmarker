package org.freshmarker.core.fragment;

import java.util.Locale;
import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.ProcessException;
import org.freshmarker.core.environment.SettingEnvironment;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.model.primitive.TemplateString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SettingFragment implements Fragment {

  private static final Logger logger = LoggerFactory.getLogger(SettingFragment.class);

  private final String name;
  private final TemplateObject expression;

  public SettingFragment(String name, TemplateObject expression) {
    this.name = name;
    this.expression = expression;
  }

  @Override
  public void process(ProcessContext context) {
    TemplateObject setting = expression.evaluateToObject(context);
    if ("locale".equals(name)) {
      String value = setting.evaluate(context, TemplateString.class).getValue();
      Locale locale = Locale.forLanguageTag(value);
      if (locale == null) {
        throw new ProcessException("unknown locale: " + value);
      }
      context.setEnvironment(new SettingEnvironment(context.getEnvironment(), locale, null));
      logger.info("new locale: {}", locale);
    }
  }
}
