package org.freshmarker.core.fragment;

import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.ProcessException;
import org.freshmarker.core.environment.SettingEnvironment;
import org.freshmarker.core.formatter.ClassicDateFormatter;
import org.freshmarker.core.formatter.ClassicDateTimeFormatter;
import org.freshmarker.core.formatter.ClassicTimeFormatter;
import org.freshmarker.core.formatter.DateFormatter;
import org.freshmarker.core.formatter.DateTimeFormatter;
import org.freshmarker.core.formatter.Formatter;
import org.freshmarker.core.formatter.TimeFormatter;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.model.date.TemplateClassicDate;
import org.freshmarker.core.model.date.TemplateClassicDateTime;
import org.freshmarker.core.model.date.TemplateClassicTime;
import org.freshmarker.core.model.primitive.TemplateString;
import org.freshmarker.core.model.temporal.TemplateLocalDate;
import org.freshmarker.core.model.temporal.TemplateLocalDateTime;
import org.freshmarker.core.model.temporal.TemplateLocalTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;
import java.util.Map;

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
            context.setEnvironment(new SettingEnvironment(context.getEnvironment(), locale, null, null));
            logger.info("new locale: {}", locale);
            return;
        }
        if ("date_format".equals(name)) {
            String value = setting.evaluate(context, TemplateString.class).getValue();
            Map<Class<? extends TemplateObject>, Formatter> formatter = Map.of(
                    TemplateLocalDate.class, new DateFormatter(value), TemplateClassicDate.class, new ClassicDateFormatter(value));
            context.setEnvironment(new SettingEnvironment(context.getEnvironment(), null, null, formatter));
            logger.info("new date format: {}", value);
            return;
        }
        if ("time_format".equals(name)) {
            String value = setting.evaluate(context, TemplateString.class).getValue();
            Map<Class<? extends TemplateObject>, Formatter> formatter = Map.of(
                    TemplateLocalTime.class, new TimeFormatter(value), TemplateClassicTime.class, new ClassicTimeFormatter(value));
            context.setEnvironment(new SettingEnvironment(context.getEnvironment(), null, null, formatter));
            logger.info("new time format: {}", value);
            return;
        }
        if ("datetime_format".equals(name)) {
            String value = setting.evaluate(context, TemplateString.class).getValue();
            Map<Class<? extends TemplateObject>, Formatter> formatter = Map.of(
                    TemplateLocalDateTime.class, new DateTimeFormatter(value), TemplateClassicDateTime.class, new ClassicDateTimeFormatter(value));
            context.setEnvironment(new SettingEnvironment(context.getEnvironment(), null, null, formatter));
            logger.info("new date-time format: {}", value);
            return;
        }
        throw new ProcessException("unknown setting: " + name);
    }
}
