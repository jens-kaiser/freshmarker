package org.freshmarker.core;

import org.freshmarker.core.directive.TemplateFunction;
import org.freshmarker.core.directive.UserDirective;
import org.freshmarker.core.formatter.Formatter;
import org.freshmarker.core.fragment.Fragment;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.output.OutputFormat;

import java.io.Writer;
import java.time.ZoneId;
import java.util.Locale;
import java.util.Optional;

public interface Environment {

    TemplateObject mapObject(Object object);

    TemplateObject getValue(String name);

    boolean checkVariable(String name);

    Locale getLocale();

    ZoneId getZoneId();

    OutputFormat getOutputFormat();

    UserDirective getDirective(String nameSpace, String name);

    TemplateFunction getFunction(String name);

    Writer getWriter();

    Optional<Fragment> getNestedContent();

    void createVariable(String name, TemplateObject value);

    void setVariable(String name, TemplateObject value);

    <T extends TemplateObject> Formatter getFormatter(Class<T> type);
}
