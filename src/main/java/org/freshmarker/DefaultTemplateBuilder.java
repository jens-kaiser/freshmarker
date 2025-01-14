package org.freshmarker;

import ftl.FreshMarkerParser;
import ftl.ParseException;
import ftl.ast.Root;
import org.freshmarker.Configuration.TemplateBuilder;
import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.StaticContext;
import org.freshmarker.core.directive.UserDirective;
import org.freshmarker.core.environment.BaseEnvironment;
import org.freshmarker.core.environment.NameSpaced;
import org.freshmarker.core.features.SimpleFeatureSet;
import org.freshmarker.core.features.TemplateFeature;
import org.freshmarker.core.formatter.DateFormatter;
import org.freshmarker.core.formatter.DateTimeFormatter;
import org.freshmarker.core.formatter.TimeFormatter;
import org.freshmarker.core.fragment.Fragment;
import org.freshmarker.core.ftl.FragmentBuilder;
import org.freshmarker.core.model.temporal.TemplateInstant;
import org.freshmarker.core.model.temporal.TemplateLocalDate;
import org.freshmarker.core.model.temporal.TemplateLocalDateTime;
import org.freshmarker.core.model.temporal.TemplateLocalTime;
import org.freshmarker.core.model.temporal.TemplateZonedDateTime;
import org.freshmarker.core.output.OutputFormat;
import org.freshmarker.core.output.StandardOutputFormats;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public final class DefaultTemplateBuilder implements ContextCreator, TemplateBuilder {
    private final Configuration configuration;

    private final Locale locale;
    private final ZoneId zoneId;
    private final OutputFormat outputFormat;
    private final StaticContext context;
    private final Clock clock;
    private final SimpleFeatureSet featureSet;

    DefaultTemplateBuilder(Configuration configuration, StaticContext context, Locale locale, ZoneId zoneId, OutputFormat outputFormat, Clock clock, SimpleFeatureSet featureSet) {
        this.configuration = configuration;
        this.locale = locale;
        this.zoneId = zoneId;
        this.outputFormat = outputFormat;
        this.context = context;
        this.clock = clock;
        this.featureSet = featureSet;
    }

    @Override
    public TemplateBuilder withDateTimeFormat(String pattern, ZoneId zoneId) {
        context.formatter().put(TemplateInstant.class, new DateTimeFormatter(pattern, zoneId));
        context.formatter().put(TemplateZonedDateTime.class, new DateTimeFormatter(pattern, zoneId));
        context.formatter().put(TemplateLocalDateTime.class, new DateTimeFormatter(pattern, zoneId));
        return this;
    }

    @Override
    public TemplateBuilder withDateTimeFormat(String pattern) {
        context.formatter().put(TemplateZonedDateTime.class, new DateTimeFormatter(pattern));
        context.formatter().put(TemplateLocalDateTime.class, new DateTimeFormatter(pattern));
        return this;
    }

    @Override
    public TemplateBuilder withDateFormat(String pattern) {
        context.formatter().put(TemplateLocalDate.class, new DateFormatter(pattern));
        return this;
    }

    @Override
    public TemplateBuilder withTimeFormat(String pattern) {
        context.formatter().put(TemplateLocalTime.class, new TimeFormatter(pattern));
        return this;
    }

    @Override
    public TemplateBuilder withClock(Clock clock) {
        return new DefaultTemplateBuilder(configuration, context, locale, zoneId, outputFormat, clock, featureSet);
    }

    @Override
    public TemplateBuilder withLocale(Locale locale) {
        return new DefaultTemplateBuilder(configuration, context, locale, zoneId, outputFormat, clock, featureSet);
    }

    @Override
    public TemplateBuilder withZoneId(ZoneId zoneId) {
        return new DefaultTemplateBuilder(configuration, context, locale, zoneId, outputFormat, clock, featureSet);
    }

    @Override
    public TemplateBuilder withOutputFormat(String outputFormat) {
        return withOutputFormat(context.outputs().getOrDefault(outputFormat, StandardOutputFormats.NONE));
    }

    @Override
    public TemplateBuilder withOutputFormat(OutputFormat format) {
        return new DefaultTemplateBuilder(configuration, context, locale, zoneId, format, clock, featureSet);
    }

    @Override
    public TemplateBuilder with(TemplateFeature templateFeature) {
        SimpleFeatureSet newFeatureSet = featureSet.with(templateFeature);
        if (newFeatureSet == featureSet) {
            return this;
        }
        return new DefaultTemplateBuilder(configuration, context, locale, zoneId, outputFormat, clock, newFeatureSet);
    }

    @Override
    public TemplateBuilder without(TemplateFeature templateFeature) {
        SimpleFeatureSet newFeatureSet = featureSet.without(templateFeature);
        if (newFeatureSet == featureSet) {
            return this;
        }
        return new DefaultTemplateBuilder(configuration, context, locale, zoneId, outputFormat, clock, newFeatureSet);
    }

    @Override
    public Template getTemplate(Path path) throws ParseException, IOException {
        return getTemplate(path.getParent(), path.toString(), Files.readString(path));
    }

    @Override
    public Template getTemplate(Path path, Charset charset) throws ParseException, IOException {
        return getTemplate(path.getParent(), path.toString(), Files.readString(path, charset));
    }

    @Override
    public Template getTemplate(String name, Reader reader) throws ParseException {
        return getTemplate(Path.of("."), name, new BufferedReader(reader).lines().collect(Collectors.joining("\n")));
    }

    @Override
    public Template getTemplate(String name, String content) throws ParseException {
        return getTemplate(Path.of("."), name, content);
    }

    @Override
    public Template getTemplate(Path importPath, String name, Reader reader) throws ParseException {
        return getTemplate(importPath, name, new BufferedReader(reader).lines().collect(Collectors.joining("\n")));
    }

    @Override
    public Template getTemplate(Path importPath, String name, String content) throws ParseException {
        FreshMarkerParser parser = new FreshMarkerParser(content);
        parser.setInputSource(name);
        parser.Root();
        Root root = (Root) parser.rootNode();
        new TokenLineNormalizer().normalize(root);
        Template template = new Template(this, context.templateLoader(), importPath);
        List<Fragment> fragments = root.accept(new FragmentBuilder(template, configuration, null, featureSet), new ArrayList<>());
        fragments.forEach(template.getRootFragment()::addFragment);
        return template;
    }

    public ProcessContext createContext(Map<String, Object> dataModel, Writer writer, Map<NameSpaced, UserDirective> userDirectives) {
        BaseEnvironment baseEnvironment = new BaseEnvironment(dataModel, context.providers(), configuration.getBuiltInVariableProviders(), clock);
        return new ProcessContext(context, baseEnvironment, userDirectives, outputFormat, locale, zoneId, writer);
    }
}
