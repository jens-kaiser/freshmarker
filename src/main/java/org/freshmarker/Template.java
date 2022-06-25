package org.freshmarker;

import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.environment.WrapperEnvironment;
import org.freshmarker.core.directive.UserDirective;
import org.freshmarker.core.fragment.BlockFragment;
import org.freshmarker.core.fragment.TemplateReturnException;


public final class Template {

  private final BlockFragment rootFragment = new BlockFragment();
  private final Configuration configuration;
  private final Map<String, UserDirective> userDirectives = new HashMap<>();

  public Template(Configuration configuration) {
    this.configuration = configuration;
  }

  public void process(Map<String, Object> dataModel, Writer writer) {
    ProcessContext context = configuration.createContext(dataModel, writer);
    context.setEnvironment(new WrapperEnvironment(context.getEnvironment()) {
      @Override
      public UserDirective getDirective(String name) {
        UserDirective userDirective = userDirectives.get(name);
        return userDirective != null ? userDirective : super.getDirective(name);
      }
    });
    try {
      rootFragment.process(context);
    } catch (TemplateReturnException ignored) {
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

  public Map<String, UserDirective> getUserDirectives() {
    return userDirectives;
  }
}
