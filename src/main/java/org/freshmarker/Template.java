package org.freshmarker;

import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.ReduceContext;
import org.freshmarker.core.ReduceException;
import org.freshmarker.core.directive.UserDirective;
import org.freshmarker.core.environment.NameSpaced;
import org.freshmarker.core.environment.ReducingVariableEnvironment;
import org.freshmarker.core.environment.WrapperEnvironment;
import org.freshmarker.core.fragment.BlockFragment;
import org.freshmarker.core.fragment.Fragment;
import org.freshmarker.core.fragment.TemplateReturnException;
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
    private final Configuration configuration;
    private final Map<NameSpaced, UserDirective> userDirectives = new HashMap<>();
    private final TemplateLoader templateLoader;
    private final Path path;

    public Template(Configuration configuration, TemplateLoader templateLoader, Path path) {
        this(configuration, templateLoader, path, new BlockFragment(new ArrayList<>()));
    }

    private Template(Configuration configuration, TemplateLoader templateLoader, Path path, BlockFragment rootFragment) {
        this.configuration = configuration;
        this.templateLoader = templateLoader;
        this.path = path;
        this.rootFragment = rootFragment;
    }

    public void process(Map<String, Object> dataModel, Writer writer) {
        ProcessContext context = configuration.createContext(dataModel, writer);
        context.setEnvironment(getWrapperEnvironment(context));
        try {
            rootFragment.process(context);
        } catch (TemplateReturnException e) {
            log.debug("return exception: {}", e.getMessage());
        }
    }

    public String process(Map<String, Object> dataModel) {
        StringWriter writer = new StringWriter();
        process(dataModel, writer);
        return writer.toString();
    }

    public Template reduce(Map<String, Object> dataModel) {
        return reduce(dataModel, new ReductionStatus());
    }

    public Template reduce(Map<String, Object> dataModel, ReductionStatus status) {
        status.total().set(rootFragment.getSize());
        ProcessContext context = configuration.createContext(dataModel, new StringWriter());
        context.setEnvironment(new ReducingVariableEnvironment(getWrapperEnvironment(context)));
        try {
            BlockFragment reducedFragment = toBlock(rootFragment.reduce(new ReduceContext(context, status)));
            status.deleted().set(rootFragment.getSize() - reducedFragment.getSize());
            log.info("reduced by: {}", status);
            return new Template(configuration, templateLoader, path, reducedFragment);
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

    private WrapperEnvironment getWrapperEnvironment(ProcessContext context) {
        return new WrapperEnvironment(context.getEnvironment()) {
            @Override
            public UserDirective getDirective(String nameSpace, String name) {
                UserDirective userDirective = userDirectives.get(new NameSpaced(nameSpace, name));
                return userDirective != null ? userDirective : super.getDirective(nameSpace, name);
            }
        };
    }
}
