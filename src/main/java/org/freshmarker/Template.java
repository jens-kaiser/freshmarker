package org.freshmarker;

import org.freshmarker.api.FeatureSet;
import org.freshmarker.api.TemplateLoader;
import org.freshmarker.api.UserDirective;
import org.freshmarker.core.LocalContext;
import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.ProcessException;
import org.freshmarker.core.ReduceContext;
import org.freshmarker.core.ReduceException;
import org.freshmarker.core.StaticContext;
import org.freshmarker.core.environment.BaseEnvironment;
import org.freshmarker.core.environment.DefaultTemplateObjectMapper;
import org.freshmarker.core.environment.NameSpaced;
import org.freshmarker.core.environment.ReducingVariableEnvironment;
import org.freshmarker.core.fragment.BlockFragment;
import org.freshmarker.core.fragment.Fragment;
import org.freshmarker.core.fragment.TemplateReturnException;
import org.freshmarker.core.model.TemplateMap;
import org.freshmarker.core.providers.TemplateObjectProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.StringWriter;
import java.io.Writer;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Template {

    private static final Logger log = LoggerFactory.getLogger(Template.class);

    private final BlockFragment rootFragment;
    private final Map<NameSpaced, UserDirective> userDirectives = new HashMap<>();
    private final StaticContext context;
    private final Path path;
    private final Map<String, Fragment> bricks = new HashMap<>();
    private final FeatureSet featureSet;
    private final Map<Class<?>, TemplateObjectProvider> templateObjectProviderMap;
    private final LocalContext localContext;
    private String resourceBundleName;

    Template(StaticContext context, Path path, FeatureSet featureSet, LocalContext localContext) {
        this(context, path, new BlockFragment(new ArrayList<>()), featureSet, localContext, new HashMap<>());
    }

    private Template(StaticContext context, Path path, BlockFragment rootFragment, FeatureSet featureSet, LocalContext localContext,
                     Map<Class<?>, TemplateObjectProvider> templateObjectProviderMap) {
        this.context = context;
        this.path = path;
        this.rootFragment = rootFragment;
        this.featureSet = featureSet;
        this.templateObjectProviderMap = templateObjectProviderMap;
        this.localContext = localContext;
    }

    public void addBrick(String key, Fragment fragment) {
        if (bricks.containsKey(key)) {
            throw new IllegalArgumentException("brick with identical name exists: " + key);
        }
        bricks.put(key, fragment);
    }

    public void process(Map<String, Object> dataModel, Writer writer) {
        process(dataModel, writer, rootFragment);
    }

    public void process(Object dataModel, Writer writer) {
        process(dataModel, writer, rootFragment);
    }

    public void processBrick(String brickName, Map<String, Object> dataModel, Writer writer) {
        Fragment brickFragment = bricks.get(brickName);
        if (brickFragment == null) {
            throw new ProcessException("missing brick: " + brickName);
        }
        process(dataModel, writer, brickFragment);
    }

    public void processBrick(String brickName, Object dataModel, Writer writer) {
        Fragment brickFragment = bricks.get(brickName);
        if (brickFragment == null) {
            throw new ProcessException("missing brick: " + brickName);
        }
        process(dataModel, writer, brickFragment);
    }

    private void process(Map<String, Object> dataModel, Writer writer, Fragment brickFragment) {
        DefaultTemplateObjectMapper templateObjectMapper = new DefaultTemplateObjectMapper(context.providers(), templateObjectProviderMap);
        process(writer, brickFragment, dataModel, templateObjectMapper);
    }

    private void process(Object value, Writer writer, Fragment brickFragment) {
        DefaultTemplateObjectMapper templateObjectMapper = new DefaultTemplateObjectMapper(context.providers(), templateObjectProviderMap);
        Map<String, Object> dataModel = getDataModel(value, templateObjectMapper);
        process(writer, brickFragment, dataModel, templateObjectMapper);
    }

    private void process(Writer writer, Fragment brickFragment, Map<String, Object> dataModel, DefaultTemplateObjectMapper templateObjectMapper) {
        ProcessContext processContext = createContext(this.context, dataModel, writer, userDirectives, featureSet, localContext, templateObjectMapper);
        processContext.setResourceBundle(resourceBundleName);
        try {
            brickFragment.process(processContext);
        } catch (TemplateReturnException e) {
            log.debug("return exception: {}", e.getMessage());
        }
    }

    public String processBrick(String brickName, Map<String, Object> dataModel) {
        try (StringBuilderWriter writer = new StringBuilderWriter()) {
            processBrick(brickName, dataModel, writer);
            return writer.toString();
        }
    }

    public String processBrick(String brickName, Object dataModel) {
        try (StringBuilderWriter writer = new StringBuilderWriter()) {
            processBrick(brickName, dataModel, writer);
            return writer.toString();
        }
    }

    public String process(Map<String, Object> dataModel) {
        try (StringBuilderWriter writer = new StringBuilderWriter()) {
            process(dataModel, writer);
            return writer.toString();
        }
    }

    public String process(Object dataModel) {
        try (StringBuilderWriter writer = new StringBuilderWriter()) {
            process(dataModel, writer);
            return writer.toString();
        }
    }

    public Template reduce(Map<String, Object> dataModel) {
        return reduce(dataModel, new ReductionStatus());
    }

    public Template reduce(Object dataModel) {
        return reduce(dataModel, new ReductionStatus());
    }

    public Template reduce(Map<String, Object> dataModel, ReductionStatus status) {
        status.before().set(rootFragment.getSize());
        DefaultTemplateObjectMapper templateObjectMapper = new DefaultTemplateObjectMapper(context.providers(), templateObjectProviderMap);
        return reduce(status, dataModel, templateObjectMapper);
    }

    public Template reduce(Object value, ReductionStatus status) {
        status.before().set(rootFragment.getSize());
        DefaultTemplateObjectMapper templateObjectMapper = new DefaultTemplateObjectMapper(context.providers(), templateObjectProviderMap);
        return reduce(status, getDataModel(value, templateObjectMapper), templateObjectMapper);
    }

    private Template reduce(ReductionStatus status, Map<String, Object> dataModel, DefaultTemplateObjectMapper templateObjectMapper) {
        ProcessContext processContext = createContext(context, dataModel, new StringBuilderWriter(), userDirectives, featureSet, localContext, templateObjectMapper);
        processContext.setEnvironment(new ReducingVariableEnvironment(processContext.getEnvironment()));
        try {
            BlockFragment reducedFragment = toBlock(rootFragment.reduce(new ReduceContext(processContext, status)));
            status.after().set(reducedFragment.getSize());
            log.debug("reduced by: {}", status);
            return new Template(context, path, reducedFragment, featureSet, localContext, templateObjectProviderMap);
        } catch (RuntimeException e) {
            throw new ReduceException("cannot reduce: " + e.getMessage(), e);
        }
    }

    public Template hook(Map<String, Object> dataModel) {
        Map<Class<?>, TemplateObjectProvider> currentMap = new HashMap<>(templateObjectProviderMap);
        DefaultTemplateObjectMapper templateObjectMapper = new DefaultTemplateObjectMapper(context.providers(), currentMap);
        return hook(dataModel, templateObjectMapper, currentMap);
    }

    public Template hook(Object value) {
        Map<Class<?>, TemplateObjectProvider> currentMap = new HashMap<>(templateObjectProviderMap);
        DefaultTemplateObjectMapper templateObjectMapper = new DefaultTemplateObjectMapper(context.providers(), currentMap);
        return hook(getDataModel(value, templateObjectMapper), templateObjectMapper, currentMap);
    }

    private Template hook(Map<String, Object> dataModel, DefaultTemplateObjectMapper templateObjectMapper, Map<Class<?>, TemplateObjectProvider> currentMap) {
        ProcessContext processContext = createContext(context, dataModel, new StringWriter(), userDirectives, featureSet, localContext, templateObjectMapper);
        processContext.setResourceBundle(resourceBundleName);
        try {
            rootFragment.process(processContext);
        } catch (TemplateReturnException e) {
            log.debug("return exception: {}", e.getMessage());
        }
        return new Template(context, path, rootFragment, featureSet, localContext, currentMap);
    }

    private static Map<String, Object> getDataModel(Object value, DefaultTemplateObjectMapper templateObjectMapper) {
        try {
            return ((TemplateMap) templateObjectMapper.mapObject(value)).map();
        } catch (RuntimeException e) {
            throw new ProcessException("invalid data model: " + value.getClass(), e);
        }
    }

    private BlockFragment toBlock(Fragment fragment) {
        return fragment instanceof BlockFragment blockFragment ? blockFragment : new BlockFragment(List.of(fragment));
    }

    public BlockFragment getRootFragment() {
        return rootFragment;
    }

    public Map<NameSpaced, UserDirective> getUserDirectives() {
        return userDirectives;
    }

    public TemplateLoader getTemplateLoader() {
        return localContext.templateLoader();
    }

    public Path getPath() {
        return path;
    }

    public void setResourceBundle(String resourceBundleName) {
        this.resourceBundleName = resourceBundleName;
    }

    private static ProcessContext createContext(StaticContext context, Map<String, Object> dataModel, Writer writer, Map<NameSpaced, UserDirective> userDirectives, FeatureSet featureSet, LocalContext localContext, DefaultTemplateObjectMapper templateObjectMapper) {
        BaseEnvironment baseEnvironment = new BaseEnvironment(dataModel, context.builtInVariableProviders(), localContext.clock(), templateObjectMapper);
        return new ProcessContext(context, baseEnvironment, userDirectives, writer, featureSet, templateObjectMapper, localContext);
    }
}
