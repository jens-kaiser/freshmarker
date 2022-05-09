package org.freshmarker;

import ftl.FTLParser;
import ftl.ParseException;
import ftl.ast.FTLHeader;
import ftl.ast.Root;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.function.Function;
import org.freshmarker.core.BaseEnvironment;
import org.freshmarker.core.BufferedEnvironment;
import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.TemplateLoader;
import org.freshmarker.core.TemplateNotFoundException;
import org.freshmarker.core.TemplateSource;
import org.freshmarker.core.buildin.BuiltIn;
import org.freshmarker.core.buildin.BuiltInKey;
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
import org.freshmarker.core.output.HtmlOutputFormat;
import org.freshmarker.core.output.NoEscapeFormat;
import org.freshmarker.core.output.OutputFormat;
import org.freshmarker.core.output.UndefinedOutputFormat;
import org.freshmarker.core.plugin.PluginProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Configuration {

  private static final Logger logger = LoggerFactory.getLogger(Configuration.class);

  private final Map<BuiltInKey, BuiltIn> builtIns = new HashMap<>();
  private final Map<Class<?>, Function<Object, TemplateObject>> mapper = new HashMap<>();
  private final Map<Class<? extends TemplateObject>, Formatter> formatter = new HashMap<>();
  private final Map<String, OutputFormat> outputs = new HashMap<>();
  private TemplateLoader templateLoader;
  private Locale locale;

  private String outputFormat = "undefined";

  public Configuration() {
    locale = Locale.getDefault();
    templateLoader = name -> {throw new IllegalArgumentException("no template loader configured");};

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

    outputs.put("HTML", HtmlOutputFormat.HTML);
    outputs.put("XHTML", HtmlOutputFormat.HTML);
    outputs.put("XML", HtmlOutputFormat.XML);
    outputs.put("undefined", UndefinedOutputFormat.INSTANCE);
    outputs.put("plainText", NoEscapeFormat.INSTANCE);
    outputs.put("JavaScript", NoEscapeFormat.INSTANCE);
    outputs.put("JSON", NoEscapeFormat.INSTANCE);
    outputs.put("CSS", NoEscapeFormat.INSTANCE);

    registerPlugins();
  }

  private void registerPlugins() {
    ServiceLoader.load(PluginProvider.class).forEach(this::registerPlugin);
  }

  public void registerPlugin(PluginProvider provider) {
    logger.info("register builtins: {}", provider.getClass().getSimpleName());
    provider.registerBuildIn(builtIns);
    logger.info("register formatter: {}", provider.getClass().getSimpleName());
    provider.registerFormatter(formatter);
    logger.info("register mapper: {}", provider.getClass().getSimpleName());
    provider.registerMapper(mapper);
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
      FTLParser parser = new FTLParser(reader);
      parser.setInputSource(templateSource.getName());
      parser.Root();
      Root root = (Root) parser.rootNode();
      Template template = new Template(this);
      FTLHeader ftlHeader = root.firstDescendantOfType(FTLHeader.class);
      if (ftlHeader != null) {
        logger.info("ftl header: {}", ftlHeader.getLocation());
      }
      root.accept(new FragmentBuilder(), template.getRootFragment());
      return template;
    }
  }

  public ProcessContext createContext(Map<String, Object> dataModel, Writer writer) {
    OutputFormat format = outputs.getOrDefault(outputFormat, UndefinedOutputFormat.INSTANCE);
    BaseEnvironment baseEnvironment = new BaseEnvironment(dataModel, mapper, locale, format);
    BufferedEnvironment environment = new BufferedEnvironment(baseEnvironment);
    return new ProcessContext(environment, writer, builtIns, formatter);
  }

  public void setLocale(Locale locale) {
    this.locale = locale;
  }

  public void setOutputFormat(String outputFormat) {
    this.outputFormat = outputFormat;
  }
}
