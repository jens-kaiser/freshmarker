package org.freshmarker;

import ftl.ParseException;
import org.freshmarker.core.features.TemplateFeature;
import org.freshmarker.core.output.OutputFormat;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.time.Clock;
import java.time.ZoneId;
import java.util.Locale;

public interface TemplateBuilder {
    TemplateBuilder withDateTimeFormat(String pattern, ZoneId zoneId);

    TemplateBuilder withDateTimeFormat(String pattern);

    TemplateBuilder withDateFormat(String pattern);

    TemplateBuilder withTimeFormat(String pattern);

    TemplateBuilder withClock(Clock clock);

    TemplateBuilder withLocale(Locale locale);

    TemplateBuilder withZoneId(ZoneId zoneId);

    TemplateBuilder withOutputFormat(String outputFormat);

    TemplateBuilder withOutputFormat(OutputFormat format);

    TemplateBuilder with(TemplateFeature templateFeature);

    TemplateBuilder without(TemplateFeature templateFeature);

    Template getTemplate(Path path) throws ParseException, IOException;

    Template getTemplate(Path path, Charset charset) throws ParseException, IOException;

    Template getTemplate(String name, Reader reader) throws ParseException;

    Template getTemplate(String name, String content) throws ParseException;

    Template getTemplate(Path importPath, String name, Reader reader) throws ParseException;

    Template getTemplate(Path importPath, String name, String content) throws ParseException;
}
