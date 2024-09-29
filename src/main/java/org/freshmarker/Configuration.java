package org.freshmarker;

import ftl.FreshMarkerParser;
import ftl.ParseException;
import ftl.ast.FTLHeader;
import ftl.ast.Root;
import org.freshmarker.core.ModelSecurityGateway;
import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.ProcessException;
import org.freshmarker.core.StaticContext;
import org.freshmarker.core.buildin.BuiltIn;
import org.freshmarker.core.buildin.BuiltInKey;
import org.freshmarker.core.directive.TemplateFunction;
import org.freshmarker.core.directive.UserDirective;
import org.freshmarker.core.environment.BaseEnvironment;
import org.freshmarker.core.environment.NameSpaced;
import org.freshmarker.core.formatter.BooleanFormatter;
import org.freshmarker.core.formatter.Formatter;
import org.freshmarker.core.formatter.NumberFormatter;
import org.freshmarker.core.fragment.Fragment;
import org.freshmarker.core.ftl.FragmentBuilder;
import org.freshmarker.core.model.TemplateNull;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.model.primitive.TemplateBoolean;
import org.freshmarker.core.model.primitive.TemplateNumber;
import org.freshmarker.core.model.primitive.TemplateString;
import org.freshmarker.core.output.OutputFormat;
import org.freshmarker.core.output.StandardOutputFormats;
import org.freshmarker.core.output.UndefinedOutputFormat;
import org.freshmarker.core.plugin.PluginProvider;
import org.freshmarker.core.providers.BeanTemplateObjectProvider;
import org.freshmarker.core.providers.CompoundTemplateObjectProvider;
import org.freshmarker.core.providers.MappingTemplateObjectProvider;
import org.freshmarker.core.providers.RecordTemplateObjectProvider;
import org.freshmarker.core.providers.TemplateObjectProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class Configuration {

    private static final Logger logger = LoggerFactory.getLogger(Configuration.class);

    public static class TemplateBuilder {
        private final Configuration configuration;

        private final Locale locale;
        private final ZoneId zoneId;
        private final OutputFormat outputFormat;
        private final StaticContext context;

        TemplateBuilder(Configuration configuration, StaticContext context, Locale locale, ZoneId zoneId, OutputFormat outputFormat) {
            this.configuration = configuration;
            this.locale = locale;
            this.zoneId = zoneId;
            this.outputFormat = outputFormat;
            this.context = context;
        }

        public TemplateBuilder withLocale(Locale locale) {
            return new TemplateBuilder(configuration, context, locale, zoneId, outputFormat);
        }

        public TemplateBuilder withZoneId(ZoneId zoneId) {
            return new TemplateBuilder(configuration, context, locale, zoneId, outputFormat);
        }

        public TemplateBuilder withOutputFormat(String outputFormat) {
            return withOutputFormat(context.outputs().getOrDefault(outputFormat, UndefinedOutputFormat.INSTANCE));
        }

        public TemplateBuilder withOutputFormat(OutputFormat format) {
            return new TemplateBuilder(configuration, context, locale, zoneId, format);
        }

        public Template getTemplate(Path path) throws ParseException, IOException {
            return getTemplate(path.getParent(), path.toString(), Files.readString(path));
        }

        public Template getTemplate(Path path, Charset charset) throws ParseException, IOException {
            return getTemplate(path.getParent(), path.toString(), Files.readString(path, charset));
        }

        public Template getTemplate(String name, Reader reader) throws ParseException {
            return getTemplate(Path.of("."), name, new BufferedReader(reader).lines().collect(Collectors.joining("\n")));
        }

        public Template getTemplate(String name, String content) throws ParseException {
            return getTemplate(Path.of("."), name, content);
        }

        public Template getTemplate(Path importPath, String name, Reader reader) throws ParseException {
            return getTemplate(importPath, name, new BufferedReader(reader).lines().collect(Collectors.joining("\n")));
        }

        public Template getTemplate(Path importPath, String name, String content) throws ParseException {
            FreshMarkerParser parser = new FreshMarkerParser(content);
            parser.setInputSource(name);
            parser.Root();
            Root root = (Root) parser.rootNode();
            new TokenLineNormalizer().normalize(root);
            FTLHeader ftlHeader = root.firstDescendantOfType(FTLHeader.class);
            if (ftlHeader != null) {
                throw new ProcessException("ftl header is not supported", ftlHeader);
            }
            Template template = new Template(this, context.templateLoader(), importPath);
            List<Fragment> fragments = root.accept(new FragmentBuilder(template, configuration, null), new ArrayList<>());
            fragments.forEach(template.getRootFragment()::addFragment);
            return template;
        }

        ProcessContext createContext(Map<String, Object> dataModel, Writer writer, Map<NameSpaced, UserDirective> userDirectives) {
            BaseEnvironment baseEnvironment = new BaseEnvironment(dataModel, context.providers());
            return new ProcessContext(context, baseEnvironment, List.of(userDirectives, context.userDirectives()), outputFormat, locale, zoneId, writer);
        }
    }

    private Map<BuiltInKey, BuiltIn> builtIns = new HashMap<>();
    private Map<Class<? extends TemplateObject>, Formatter> formatter = new HashMap<>();
    private Map<String, OutputFormat> outputs = new HashMap<>();
    private final MappingTemplateObjectProvider mappingTemplateObjectProvider = new MappingTemplateObjectProvider();
    private final ModelSecurityGateway modelSecurityGateway = new ModelSecurityGateway();
    private final List<TemplateObjectProvider> providers;
    private final Map<NameSpaced, UserDirective> userDirectives = new HashMap<>();
    private final Map<String, TemplateFunction> functions = new HashMap<>();

    private TemplateLoader templateLoader;

    public enum FeatureFlag {
        REFLECTIONS,
        LAMBDAS
    }

    public Configuration() {
        this(FeatureFlag.LAMBDAS);
    }

    public Configuration(FeatureFlag featureFlag) {
        modelSecurityGateway.addForbiddenPackages("java", "javax", "sun", "com.sun");
        BeanTemplateObjectProvider beanTemplateObjectProvider = new BeanTemplateObjectProvider(featureFlag, modelSecurityGateway);
        RecordTemplateObjectProvider recordTemplateObjectProvider = new RecordTemplateObjectProvider(featureFlag, modelSecurityGateway);
        providers = new ArrayList<>(List.of(mappingTemplateObjectProvider, recordTemplateObjectProvider, new CompoundTemplateObjectProvider(), beanTemplateObjectProvider));

        templateLoader = new DefaultFileSystemTemplateLoader();
        mappingTemplateObjectProvider.addMapper(String.class, o -> new TemplateString((String) o));
        mappingTemplateObjectProvider.addMapper(Long.class, o -> new TemplateNumber((Long) o));
        mappingTemplateObjectProvider.addMapper(Integer.class, o -> TemplateNumber.of((Integer) o));
        mappingTemplateObjectProvider.addMapper(Short.class, o -> new TemplateNumber((Short) o));
        mappingTemplateObjectProvider.addMapper(Byte.class, o -> new TemplateNumber((Byte) o));
        mappingTemplateObjectProvider.addMapper(Double.class, o -> new TemplateNumber((Double) o));
        mappingTemplateObjectProvider.addMapper(Float.class, o -> new TemplateNumber((Float) o));
        mappingTemplateObjectProvider.addMapper(Boolean.class, o -> Boolean.TRUE.equals(o) ? TemplateBoolean.TRUE : TemplateBoolean.FALSE);

        formatter.put(TemplateNumber.class, new NumberFormatter());
        formatter.put(TemplateBoolean.class, new BooleanFormatter("yes", "no"));

        outputs.put("HTML", StandardOutputFormats.HTML);
        outputs.put("XHTML", StandardOutputFormats.HTML);
        outputs.put("XML", StandardOutputFormats.XML);
        outputs.put("plainText", StandardOutputFormats.NONE);
        outputs.put("JavaScript", StandardOutputFormats.JAVASCRIPT);
        outputs.put("JSON", StandardOutputFormats.NONE);
        outputs.put("CSS", StandardOutputFormats.CSS);
        outputs.put("ADOC", StandardOutputFormats.ADOC);

        registerPlugins();
        registerSimpleMapping(StringBuilder.class, StringBuffer.class, URI.class, URL.class, UUID.class);
    }

    public void registerOutputFormat(String name, OutputFormat format) {
        Map<String, OutputFormat> newOutputs = new HashMap<>(outputs);
        newOutputs.put(Objects.requireNonNull(name), Objects.requireNonNull(format));
        outputs = newOutputs;
    }

    public void registerSimpleMapping(Class<?>... types) {
        for (Class<?> type : types) {
            mappingTemplateObjectProvider.addMapper(type, o -> new TemplateString(o.toString()));
        }
    }

    public void registerSimpleMapping(Class<?> type, Function<Object, String> mapping) {
        Objects.requireNonNull(mapping);
        mappingTemplateObjectProvider.addMapper(type, x -> {
            String apply = mapping.apply(x);
            return apply == null ? TemplateNull.NULL : new TemplateString(apply);
        });
    }

    public void registerUserDirective(String name, UserDirective directive) {
        userDirectives.put(new NameSpaced(null, name), directive);
    }

    public void registerFunction(String name, TemplateFunction function) {
        functions.put(name, function);
    }

    private void registerPlugins() {
        ServiceLoader.load(PluginProvider.class).forEach(this::registerPlugin);
    }

    public void registerPlugin(PluginProvider provider) {
        logger.debug("register plugin: {}", provider.getClass().getSimpleName());
        Map<BuiltInKey, BuiltIn> registerBuiltIns = new HashMap<>(this.builtIns);
        provider.registerBuildIn(registerBuiltIns);
        this.builtIns = registerBuiltIns;
        Map<Class<? extends TemplateObject>, Formatter> registerFormatter = new HashMap<>(this.formatter);
        provider.registerFormatter(registerFormatter);
        this.formatter = registerFormatter;
        Map<Class<?>, Function<Object, TemplateObject>> mapper = new HashMap<>();
        provider.registerMapper(mapper);
        mapper.forEach(mappingTemplateObjectProvider::addMapper);
        List<TemplateObjectProvider> list = new ArrayList<>();
        provider.registerTemplateObjectProvider(list);
        providers.addAll(providers.size() - 2, list);
        Map<String, UserDirective> additionalDirectives = new HashMap<>();
        provider.registerUserDirective(additionalDirectives);
        additionalDirectives.forEach((k, v) -> userDirectives.put(new NameSpaced(null, k), v));
        Map<String, TemplateFunction> additionalFunctions = new HashMap<>();
        provider.registerFunction(additionalFunctions);
        functions.putAll(additionalFunctions);
    }

    public TemplateBuilder builder() {
        return new TemplateBuilder(this, getContext(), Locale.getDefault(), ZoneId.systemDefault(), UndefinedOutputFormat.INSTANCE);
    }

    /**
     * @deprecated in favour of the TemplateBuilder#getTemplate call
     */
    @Deprecated(forRemoval = true, since = "1.4.6")
    public Template getTemplate(String name, String content) throws ParseException {
        return builder().getTemplate(Path.of("."), name, content);
    }

    public void setTemplateLoader(TemplateLoader templateLoader) {
        this.templateLoader = templateLoader;
    }

    public ModelSecurityGateway getSecurity() {
        return modelSecurityGateway;
    }

    public StaticContext getContext() {
        return new StaticContext(builtIns, formatter, outputs, providers, userDirectives, templateLoader, functions);
    }
}
