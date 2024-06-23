package org.freshmarker;

import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.directive.UserDirective;
import org.freshmarker.core.environment.NameSpaced;
import org.freshmarker.core.environment.WrapperEnvironment;
import org.freshmarker.core.fragment.BlockFragment;
import org.freshmarker.core.fragment.TemplateReturnException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.StringWriter;
import java.io.Writer;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public final class Template {

    private static final Logger log = LoggerFactory.getLogger(Template.class);

    private final BlockFragment rootFragment = new BlockFragment();
    private final Configuration configuration;
    private final Map<NameSpaced, UserDirective> userDirectives = new HashMap<>();
    private final TemplateLoader templateLoader;
    private final Path path;

    public Template(Configuration configuration, TemplateLoader templateLoader, Path path) {
        this.configuration = configuration;
        this.templateLoader = templateLoader;
        this.path = path;
    }

    public void process(Map<String, Object> dataModel, Writer writer) {
        ProcessContext context = configuration.createContext(dataModel, writer);
        context.setEnvironment(new WrapperEnvironment(context.getEnvironment()) {
            @Override
            public UserDirective getDirective(String nameSpace, String name) {
                UserDirective userDirective = userDirectives.get(new NameSpaced(nameSpace, name));
                return userDirective != null ? userDirective : super.getDirective(nameSpace, name);
            }
        });
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
}
