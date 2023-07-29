package org.freshmarker;

import ftl.FTLParser;
import ftl.ParseException;
import ftl.ast.FTLHeader;
import ftl.ast.Root;
import org.freshmarker.core.Environment;
import org.freshmarker.core.ModelSecurityGateway;
import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.ProcessException;
import org.freshmarker.core.TemplateLoader;
import org.freshmarker.core.TemplateNotFoundException;
import org.freshmarker.core.TemplateSource;
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
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.model.number.ByteNumber;
import org.freshmarker.core.model.number.DoubleNumber;
import org.freshmarker.core.model.number.FloatNumber;
import org.freshmarker.core.model.number.IntegerNumber;
import org.freshmarker.core.model.number.LongNumber;
import org.freshmarker.core.model.number.ShortNumber;
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

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.function.Function;

public final class Configuration {

    private static final Logger logger = LoggerFactory.getLogger(Configuration.class);

    private final Map<BuiltInKey, BuiltIn> builtIns = new HashMap<>();
    private final Map<Class<? extends TemplateObject>, Formatter> formatter = new HashMap<>();
    private final Map<String, OutputFormat> outputs = new HashMap<>();
    private final MappingTemplateObjectProvider mappingTemplateObjectProvider = new MappingTemplateObjectProvider();
    private TemplateLoader templateLoader;
    private Locale locale;
    private final ModelSecurityGateway modelSecurityGateway = new ModelSecurityGateway();
    private final List<TemplateObjectProvider> providers = new ArrayList<>(
            List.of(mappingTemplateObjectProvider, new RecordTemplateObjectProvider(), new CompoundTemplateObjectProvider(), new BeanTemplateObjectProvider(modelSecurityGateway)));
    private final Map<String, UserDirective> userDirectives = new HashMap<>();
    private final Map<String, TemplateFunction> functions = new HashMap<>();

    private String outputFormat = "undefined";


    public Configuration() {
        locale = Locale.getDefault();
        templateLoader = name -> {
            throw new ProcessException("no template loader configured");
        };

        Map<Class<?>, Function<Object, TemplateObject>> mapper = mappingTemplateObjectProvider.getMapper();
        mapper.put(String.class, o -> new TemplateString((String) o));
        mapper.put(Long.class, o -> new TemplateNumber(new LongNumber((Long) o)));
        mapper.put(Integer.class, o -> new TemplateNumber(new IntegerNumber((Integer) o)));
        mapper.put(Short.class, o -> new TemplateNumber(new ShortNumber((Short) o)));
        mapper.put(Byte.class, o -> new TemplateNumber(new ByteNumber((Byte) o)));
        mapper.put(Double.class, o -> new TemplateNumber(new DoubleNumber((Double) o)));
        mapper.put(Float.class, o -> new TemplateNumber(new FloatNumber((Float) o)));
        mapper.put(Boolean.class, o -> Boolean.TRUE.equals(o) ? TemplateBoolean.TRUE : TemplateBoolean.FALSE);

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
        provider.registerMapper(mappingTemplateObjectProvider.getMapper());
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

    public void registerTemplateLoader(TemplateLoader templateLoader) {
        this.templateLoader = templateLoader;
    }

    public Template getTemplate(String name) throws IOException, ParseException {
        return getTemplate(name, StandardCharsets.UTF_8);
    }

    public Template getTemplate(String name, Charset charset) throws ParseException, IOException {
        try (TemplateSource templateSource = templateLoader.getTemplate(name)
                .orElseThrow(() -> new TemplateNotFoundException("template not found: " + name));
             Reader reader = templateSource.getReader(charset)) {
            return getTemplate(name, reader);
        }
    }

    public Template getTemplate(String name, Reader reader) throws ParseException {
        FTLParser parser = new FTLParser(reader);
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

    public Template getTemplate(String name, String content) throws ParseException {
        return getTemplate(name, new StringReader(content));
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
}
