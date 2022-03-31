package org.freshmarker;

import ftl.FTLParser;
import ftl.ParseException;
import ftl.ast.FTLHeader;
import ftl.ast.Root;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.function.Function;
import org.freshmarker.core.BaseEnvironment;
import org.freshmarker.core.BufferedEnvironment;
import org.freshmarker.core.Environment;
import org.freshmarker.core.TemplateLoader;
import org.freshmarker.core.TemplateNotFoundException;
import org.freshmarker.core.buildin.BuildInKey;
import org.freshmarker.core.buildin.TypedBuildIn;
import org.freshmarker.core.formatter.BooleanFormatter;
import org.freshmarker.core.formatter.Formatter;
import org.freshmarker.core.formatter.NumberFormatter;
import org.freshmarker.core.ftl.FragmentBuilder;
import org.freshmarker.core.model.primitive.TemplateBoolean;
import org.freshmarker.core.model.primitive.TemplateNumber;
import org.freshmarker.core.model.primitive.TemplateNumber.Type;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.model.primitive.TemplateString;
import org.freshmarker.core.plugin.PluginProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Configuration {

  private static final Logger logger = LoggerFactory.getLogger(Configuration.class);

  private final Map<BuildInKey, TypedBuildIn> buildIns = new HashMap<>();
  private final Map<Class<?>, Function<Object, TemplateObject>> mapper = new HashMap<>();
  private final Map<Class<? extends TemplateObject>, Formatter> formatter = new HashMap<>();
  private TemplateLoader templateLoader;
  private Locale locale;

  public Configuration() {
    locale = Locale.getDefault();
    templateLoader = name -> {throw new IllegalArgumentException("no template loader configured");};

    mapper.put(String.class, o -> new TemplateString((String) o));
    mapper.put(Long.class, o -> new TemplateNumber((Number) o, Type.LONG));
    mapper.put(long.class, o -> new TemplateNumber((Number) o, Type.LONG));
    mapper.put(Integer.class, o -> new TemplateNumber((Number) o, Type.INTEGER));
    mapper.put(int.class, o -> new TemplateNumber((Number) o, Type.INTEGER));
    mapper.put(Short.class, o -> new TemplateNumber((Number) o, Type.SHORT));
    mapper.put(short.class, o -> new TemplateNumber((Number) o, Type.SHORT));
    mapper.put(Byte.class, o -> new TemplateNumber((Number) o, Type.BYTE));
    mapper.put(byte.class, o -> new TemplateNumber((Number) o, Type.BYTE));
    mapper.put(Double.class, o -> new TemplateNumber((Number) o, Type.DOUBLE));
    mapper.put(double.class, o -> new TemplateNumber((Number) o, Type.DOUBLE));
    mapper.put(Float.class, o -> new TemplateNumber((Number) o, Type.FLOAT));
    mapper.put(float.class, o -> new TemplateNumber((Number) o, Type.FLOAT));
    mapper.put(Boolean.class, o -> Boolean.TRUE.equals(o) ? TemplateBoolean.TRUE : TemplateBoolean.FALSE);
    mapper.put(boolean.class, o -> Boolean.TRUE.equals(o) ? TemplateBoolean.TRUE : TemplateBoolean.FALSE);

    formatter.put(TemplateNumber.class, new NumberFormatter());
    formatter.put(TemplateBoolean.class, new BooleanFormatter("yes", "no"));

    registerPlugins();

  }

  private void registerPlugins() {
    ServiceLoader.load(PluginProvider.class).forEach(this::registerPlugin);
  }

  public void registerPlugin(PluginProvider provider) {
    logger.info("register buildins: {}", provider.getClass().getSimpleName());
    provider.registerBuildIn(buildIns);
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
    try (Reader reader = templateLoader.getTemplate(name).map(t -> t.getReader(charset))
        .orElseThrow(() -> new TemplateNotFoundException("template not found: " + name))) {
      FTLParser parser = new FTLParser(reader);
      parser.Root();
      Root root = (Root)parser.rootNode();
      Template template = new Template(this);
      FTLHeader ftlHeader = root.firstDescendantOfType(FTLHeader.class);
      if (ftlHeader != null) {
        logger.info("ftl header: {}", ftlHeader.getLocation());
      }
      root.accept(new FragmentBuilder(), template.getRootFragment());
      return template;
    }
  }

  public Environment createEnvironment(Map<String, Object> dataModel) {
    return new BufferedEnvironment(new BaseEnvironment(buildIns, dataModel, mapper, formatter, locale));
  }

  public void setLocale(Locale locale) {
    this.locale = locale;
  }
}
