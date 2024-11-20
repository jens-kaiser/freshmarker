package org.freshmarker;

import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.ProcessException;
import org.freshmarker.core.ReduceContext;
import org.freshmarker.core.ReduceException;
import org.freshmarker.core.directive.UserDirective;
import org.freshmarker.core.environment.NameSpaced;
import org.freshmarker.core.environment.ReducingVariableEnvironment;
import org.freshmarker.core.fragment.BlockFragment;
import org.freshmarker.core.fragment.Fragment;
import org.freshmarker.core.fragment.TemplateReturnException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private final ContextCreator contextCreator;
    private final TemplateLoader templateLoader;
    private final Path path;
    private final Map<String, Fragment> bricks = new HashMap<>();
    private String resourceBundleName;

    Template(ContextCreator contextCreator, TemplateLoader templateLoader, Path path) {
        this(contextCreator, templateLoader, path, new BlockFragment(new ArrayList<>()));
    }

    private Template(ContextCreator contextCreator, TemplateLoader templateLoader, Path path, BlockFragment rootFragment) {
        this.contextCreator = contextCreator;
        this.templateLoader = templateLoader;
        this.path = path;
        this.rootFragment = rootFragment;
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

    public void processBrick(String brickName, Map<String, Object> dataModel, Writer writer) {
        Fragment brickFragment = bricks.get(brickName);
        if (brickFragment == null) {
            throw new ProcessException("missing brick: " + brickName);
        }
        process(dataModel, writer, brickFragment);
    }

    private void process(Map<String, Object> dataModel, Writer writer, Fragment brickFragment) {
        ProcessContext context = contextCreator.createContext(dataModel, writer, userDirectives);
        context.setResourceBundle(resourceBundleName);
        try {
            brickFragment.process(context);
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

    public String process(Map<String, Object> dataModel) {
        try (StringBuilderWriter writer = new StringBuilderWriter()) {
            process(dataModel, writer);
            return writer.toString();
        }
    }

    public Template reduce(Map<String, Object> dataModel) {
        return reduce(dataModel, new ReductionStatus());
    }

    public Template reduce(Map<String, Object> dataModel, ReductionStatus status) {
        status.total().set(rootFragment.getSize());
        ProcessContext context = contextCreator.createContext(dataModel, new StringBuilderWriter(), userDirectives);
        context.setEnvironment(new ReducingVariableEnvironment(context.getEnvironment()));
        try {
            BlockFragment reducedFragment = toBlock(rootFragment.reduce(new ReduceContext(context, status)));
            status.deleted().set(rootFragment.getSize() - reducedFragment.getSize());
            log.debug("reduced by: {}", status);
            return new Template(contextCreator, templateLoader, path, reducedFragment);
        } catch (RuntimeException e) {
            throw new ReduceException("cannot reduce: " + e.getMessage(), e);
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
        return templateLoader;
    }

    public Path getPath() {
        return path;
    }

    public void setResourceBundle(String resourceBundleName) {
        this.resourceBundleName = resourceBundleName;
    }
}
