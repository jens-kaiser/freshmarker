package org.freshmarker;

import ftl.FreshMarkerParser;
import ftl.ParseException;
import ftl.ast.FTLHeader;
import ftl.ast.Root;
import org.freshmarker.core.Environment;
import org.freshmarker.core.ModelSecurityGateway;
import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.buildin.BuiltIn;
import org.freshmarker.core.buildin.BuiltInKey;
import org.freshmarker.core.directive.TemplateFunction;
import org.freshmarker.core.directive.UserDirective;
import org.freshmarker.core.environment.BaseEnvironment;
import org.freshmarker.core.environment.BufferedEnvironment;
import org.freshmarker.core.environment.VariableEnvironment;
import org.freshmarker.core.formatter.BooleanFormatter;
import org.freshmarker.core.formatter.Formatter;
import org.freshmarker.core.formatter.NumberFormatter;
import org.freshmarker.core.ftl.FragmentBuilder;
import org.freshmarker.core.model.TemplateNull;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.model.primitive.TemplateBoolean;
import org.freshmarker.core.model.primitive.TemplateNumber;
import org.freshmarker.core.model.primitive.TemplateString;
import org.freshmarker.core.output.OutputFormat;
import org.freshmarker.core.output.OutputFormatBuilder;
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
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class Configuration {

    private static final Logger logger = LoggerFactory.getLogger(Configuration.class);

    private final Map<BuiltInKey, BuiltIn> builtIns = new HashMap<>();
    private final Map<Class<? extends TemplateObject>, Formatter> formatter = new HashMap<>();
    private final Map<String, OutputFormat> outputs = new HashMap<>();
    private final MappingTemplateObjectProvider mappingTemplateObjectProvider = new MappingTemplateObjectProvider();
    private Locale locale;
    private final ModelSecurityGateway modelSecurityGateway = new ModelSecurityGateway();
    private final List<TemplateObjectProvider> providers = new ArrayList<>(
            List.of(mappingTemplateObjectProvider, new RecordTemplateObjectProvider(), new CompoundTemplateObjectProvider(), new BeanTemplateObjectProvider(modelSecurityGateway)));
    private final Map<String, UserDirective> userDirectives = new HashMap<>();
    private final Map<String, TemplateFunction> functions = new HashMap<>();

    private String outputFormat = "undefined";
    private FileSystem fileSystem;

    public Configuration() {
        locale = Locale.getDefault();
        fileSystem = FileSystems.getDefault();

        mappingTemplateObjectProvider.addMapper(String.class, o -> new TemplateString((String) o));
        mappingTemplateObjectProvider.addMapper(Long.class, o -> new TemplateNumber((Long) o));
        mappingTemplateObjectProvider.addMapper(Integer.class, o -> new TemplateNumber((Integer) o));
        mappingTemplateObjectProvider.addMapper(Short.class, o -> new TemplateNumber((Short) o));
        mappingTemplateObjectProvider.addMapper(Byte.class, o -> new TemplateNumber((Byte) o));
        mappingTemplateObjectProvider.addMapper(Double.class, o -> new TemplateNumber((Double) o));
        mappingTemplateObjectProvider.addMapper(Float.class, o -> new TemplateNumber((Float) o));
        mappingTemplateObjectProvider.addMapper(Boolean.class, o -> Boolean.TRUE.equals(o) ? TemplateBoolean.TRUE : TemplateBoolean.FALSE);

        formatter.put(TemplateNumber.class, new NumberFormatter());
        formatter.put(TemplateBoolean.class, new BooleanFormatter("yes", "no"));

        OutputFormat html = new OutputFormatBuilder().withEscape('<', "&lt;").withEscape('>', "&gt;").withEscape('"', "&quot;").withEscape('&', "&amph;").withEscape('\'', "&#39;").withComment("<!-- ", " -->").build();
        OutputFormat xml = new OutputFormatBuilder().withEscape('<', "&lt;").withEscape('>', "&gt;").withEscape('"', "&quot;").withEscape('&', "&amph;").withEscape('\'', "&apos;").withComment("<!-- ", " -->").build();
        OutputFormat none = new OutputFormat() {
        };
        outputs.put("HTML", html);
        outputs.put("XHTML", html);
        outputs.put("XML", xml);
        outputs.put("plainText", none);
        outputs.put("JavaScript", new OutputFormatBuilder().withComment("/* ", " */").build());
        outputs.put("JSON", none);
        outputs.put("CSS", new OutputFormatBuilder().withComment("/* ", " */").build());
        outputs.put("ADOC", new OutputFormatBuilder().withComment("\n////\n", "\n////\n").build());

        modelSecurityGateway.addForbiddenPackages("java", "javax", "sun", "com.sun");
        registerPlugins();
    }

    public void registerSimpleMapping(Class<?> type) {
        registerSimpleMapping(type, Object::toString);
    }

    public void registerSimpleMapping(Class<?> type, Function<Object, String> mapping) {
        Objects.requireNonNull(mapping);
        mappingTemplateObjectProvider.addMapper(type, x -> {
            String apply = mapping.apply(x);
            return apply == null ? TemplateNull.NULL : new TemplateString(apply);
        });
    }

    public void registerUserDirective(String name, UserDirective directive) {
        userDirectives.put(name, directive);
    }

    public void registerFunction(String name, TemplateFunction function) {
        functions.put(name, function);
    }

    private void registerPlugins() {
        ServiceLoader.load(PluginProvider.class).forEach(this::registerPlugin);
    }

    public void registerPlugin(PluginProvider provider) {
        logger.info("register plugin: {}", provider.getClass().getSimpleName());
        provider.registerBuildIn(builtIns);
        provider.registerFormatter(formatter);
        Map<Class<?>, Function<Object, TemplateObject>> mapper = new HashMap<>();
        provider.registerMapper(mapper);
        mapper.forEach(mappingTemplateObjectProvider::addMapper);
        List<TemplateObjectProvider> list = new ArrayList<>();
        provider.registerTemplateObjectProvider(list);
        providers.addAll(providers.size() - 2, list);
        Map<String, UserDirective> additionalDirectives = new HashMap<>();
        provider.registerUserDirective(additionalDirectives);
        userDirectives.putAll(additionalDirectives);
        Map<String, TemplateFunction> additionalFunctions = new HashMap<>();
        provider.registerFunction(additionalFunctions);
        functions.putAll(additionalFunctions);
    }

    public Template getTemplate(String name, Path path) throws ParseException, IOException {
        return getTemplate(name, Files.readString(path));
    }

    public Template getTemplate(String name, Reader reader) throws ParseException {
        return getTemplate(name, new BufferedReader(reader).lines().collect(Collectors.joining("\n")));
    }

    public Template getTemplate(String name, String content) throws ParseException {
        FreshMarkerParser parser = new FreshMarkerParser(content);
        parser.setInputSource(name);
        parser.Root();
        Root root = (Root) parser.rootNode();
        new TokenLineNormalizer().normalize(root);
        Template template = new Template(this);
        FTLHeader ftlHeader = root.firstDescendantOfType(FTLHeader.class);
        if (ftlHeader != null) {
            logger.info("ftl header: {}", ftlHeader.getLocation());
        }
        root.accept(new FragmentBuilder(template), template.getRootFragment());
        return template;
    }

    public ProcessContext createContext(Map<String, Object> dataModel, Writer writer) {
        OutputFormat format = outputs.getOrDefault(outputFormat, UndefinedOutputFormat.INSTANCE);
        BaseEnvironment baseEnvironment = new BaseEnvironment(dataModel, providers, locale, format, userDirectives, functions, writer, Map.copyOf(formatter));
        Environment environment = new VariableEnvironment(new BufferedEnvironment(baseEnvironment));
        return new ProcessContext(environment, Map.copyOf(builtIns), Map.copyOf(outputs));
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public void setOutputFormat(String outputFormat) {
        this.outputFormat = outputFormat;
    }

        public void setFileSystem(FileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }

    public FileSystem getFileSystem() {
        return fileSystem;
    }
}
