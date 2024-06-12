package org.freshmarker.core.environment;

import org.freshmarker.core.Environment;
import org.freshmarker.core.formatter.Formatter;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.output.OutputFormat;

import java.time.ZoneId;
import java.util.Locale;
import java.util.Objects;

public class SettingEnvironment extends WrapperEnvironment {

    private final Settings settings;

    public SettingEnvironment(Environment wrapped, Settings settings) {
        super(wrapped);
        this.settings = settings;
    }

    @Override
    public ZoneId getZoneId() {
        return Objects.requireNonNullElseGet(settings.zoneId(), wrapped::getZoneId);
    }

    @Override
    public Locale getLocale() {
        return Objects.requireNonNullElseGet(settings.locale(), wrapped::getLocale);
    }

    @Override
    public OutputFormat getOutputFormat() {
        return Objects.requireNonNullElseGet(settings.format(), wrapped::getOutputFormat);
    }

    @Override
    public <T extends TemplateObject> Formatter getFormatter(Class<T> type) {
        Formatter formatter = settings.formatters().get(type);
        return Objects.requireNonNullElseGet(formatter, () -> wrapped.getFormatter(type));
    }
}
