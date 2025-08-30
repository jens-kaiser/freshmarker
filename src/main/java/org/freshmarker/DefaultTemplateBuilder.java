package org.freshmarker;

import ftl.FreshMarkerParser;
import ftl.ParseException;
import ftl.ast.Root;
import org.freshmarker.api.Formatter;
import org.freshmarker.api.TemplateFeature;
import org.freshmarker.core.LocalContext;
import org.freshmarker.core.StaticContext;
import org.freshmarker.core.extension.ExtensionRegistry;
import org.freshmarker.core.features.SimpleFeatureSet;
import org.freshmarker.core.formatter.DateFormatter;
import org.freshmarker.core.formatter.DateTimeFormatter;
import org.freshmarker.core.formatter.TimeFormatter;
import org.freshmarker.core.fragment.Fragment;
import org.freshmarker.core.fragment.Fragments;
import org.freshmarker.core.ftl.FragmentBuilder;
import org.freshmarker.core.ftl.TemplateDictionary;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.model.temporal.TemplateInstant;
import org.freshmarker.core.model.temporal.TemplateLocalDate;
import org.freshmarker.core.model.temporal.TemplateLocalDateTime;
import org.freshmarker.core.model.temporal.TemplateLocalTime;
import org.freshmarker.core.model.temporal.TemplateOffsetDateTime;
import org.freshmarker.core.model.temporal.TemplateZonedDateTime;
import org.freshmarker.api.OutputFormat;
import org.freshmarker.core.output.StandardOutputFormats;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public final class DefaultTemplateBuilder implements TemplateBuilder {
    private final Locale locale;
    private final ZoneId zoneId;
    private final OutputFormat outputFormat;
    private final Clock clock;
    private final SimpleFeatureSet featureSet;
    private final Map<Class<? extends TemplateObject>, Formatter> formatter;
    private final ExtensionRegistry registry;
    private final org.freshmarker.api.TemplateLoader templateLoader;

    DefaultTemplateBuilder(StaticContext context, SimpleFeatureSet featureSet, org.freshmarker.api.TemplateLoader templateLoader) {
        this.locale = Locale.getDefault();
        this.zoneId = ZoneId.systemDefault();
        this.outputFormat = StandardOutputFormats.NONE;
        this.clock = Clock.systemUTC();
        this.featureSet = featureSet;
        this.formatter = new HashMap<>();
        registry = context.registry();
        this.templateLoader = templateLoader;
    }

    private DefaultTemplateBuilder(DefaultTemplateBuilder builder, Locale locale, ZoneId zoneId, OutputFormat outputFormat, Clock clock, SimpleFeatureSet featureSet) {
        this.locale = locale;
        this.zoneId = zoneId;
        this.outputFormat = outputFormat;
        this.clock = clock;
        this.featureSet = featureSet;
        this.formatter = new HashMap<>();
        this.registry = builder.registry;
        this.templateLoader = builder.templateLoader;
    }

    public TemplateBuilder withDateTimeFormat(String pattern, ZoneId zoneId) {
        DefaultTemplateBuilder newBuilder =  new DefaultTemplateBuilder(this, locale, zoneId, outputFormat, clock, featureSet);
        newBuilder.formatter.put(TemplateInstant.class, new DateTimeFormatter(pattern, zoneId));
        newBuilder.formatter.put(TemplateZonedDateTime.class, new DateTimeFormatter(pattern, zoneId));
        newBuilder.formatter.put(TemplateOffsetDateTime.class, new DateTimeFormatter(pattern, zoneId));
        newBuilder.formatter.put(TemplateLocalDateTime.class, new DateTimeFormatter(pattern, zoneId));
        return newBuilder;
    }

    public TemplateBuilder withDateTimeFormat(String pattern) {
        DefaultTemplateBuilder newBuilder =  new DefaultTemplateBuilder(this, locale, zoneId, outputFormat, clock, featureSet);
        newBuilder.formatter.put(TemplateZonedDateTime.class, new DateTimeFormatter(pattern));
        newBuilder.formatter.put(TemplateOffsetDateTime.class, new DateTimeFormatter(pattern));
        newBuilder.formatter.put(TemplateLocalDateTime.class, new DateTimeFormatter(pattern));
        return newBuilder;
    }

    public TemplateBuilder withDateFormat(String pattern) {
        DefaultTemplateBuilder newBuilder =  new DefaultTemplateBuilder(this, locale, zoneId, outputFormat, clock, featureSet);
        newBuilder.formatter.put(TemplateLocalDate.class, new DateFormatter(pattern));
        return newBuilder;
    }

    public TemplateBuilder withTimeFormat(String pattern) {
        DefaultTemplateBuilder newBuilder =  new DefaultTemplateBuilder(this, locale, zoneId, outputFormat, clock, featureSet);
        newBuilder.formatter.put(TemplateLocalTime.class, new TimeFormatter(pattern, zoneId));
        return newBuilder;
    }

    @Override
    public TemplateBuilder withClock(Clock clock) {
        return new DefaultTemplateBuilder(this, locale, zoneId, outputFormat, clock, featureSet);
    }

    @Override
    public TemplateBuilder withLocale(Locale locale) {
        return new DefaultTemplateBuilder(this, locale, zoneId, outputFormat, clock, featureSet);
    }

    @Override
    public TemplateBuilder withZoneId(ZoneId zoneId) {
        return new DefaultTemplateBuilder(this, locale, zoneId, outputFormat, clock, featureSet);
    }

    @Override
    public TemplateBuilder withOutputFormat(String outputFormat) {
        return withOutputFormat(registry.getOutputFormats().getOrDefault(outputFormat, StandardOutputFormats.NONE));
    }

    @Override
    public TemplateBuilder withOutputFormat(OutputFormat format) {
        return new DefaultTemplateBuilder(this, locale, zoneId, format, clock, featureSet);
    }

    @Override
    public TemplateBuilder with(TemplateFeature templateFeature) {
        SimpleFeatureSet newFeatureSet = featureSet.with(templateFeature);
        if (newFeatureSet == featureSet) {
            return this;
        }
        return new DefaultTemplateBuilder(this, locale, zoneId, outputFormat, clock, newFeatureSet);
    }

    @Override
    public TemplateBuilder with(TemplateFeature templateFeature, Object parameter) {
        SimpleFeatureSet newFeatureSet = featureSet.with(templateFeature, parameter);
        if (newFeatureSet == featureSet) {
            return this;
        }
        return new DefaultTemplateBuilder(this, locale, zoneId, outputFormat, clock, newFeatureSet);
    }

    @Override
    public TemplateBuilder without(TemplateFeature templateFeature) {
        SimpleFeatureSet newFeatureSet = featureSet.without(templateFeature);
        if (newFeatureSet == featureSet) {
            return this;
        }
        return new DefaultTemplateBuilder(this, locale, zoneId, outputFormat, clock, newFeatureSet);
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
        ExtensionRegistry extensionRegistry = new ExtensionRegistry(registry, featureSet);
        Map<Class<? extends TemplateObject>, Formatter> combinedFormatters = extensionRegistry.getFormatterRegistry();
        combinedFormatters.putAll(this.formatter);
        StaticContext templateContext = new StaticContext(extensionRegistry, combinedFormatters);
        LocalContext localContext = new LocalContext(locale, outputFormat, zoneId, clock, templateLoader);
        SimpleFeatureSet featureSetCopy = new SimpleFeatureSet(featureSet);
        Template template = new Template(templateContext, importPath, featureSetCopy, localContext);
        List<Fragment> fragments = root.accept(new FragmentBuilder(template, null, featureSetCopy, 0, templateContext, new TemplateDictionary()), new ArrayList<>());
        Fragments.withVariableContext(fragments).forEach(template.getRootFragment()::addFragment);
        return template;
    }
}
