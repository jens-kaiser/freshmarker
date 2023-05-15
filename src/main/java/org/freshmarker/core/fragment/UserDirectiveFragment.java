package org.freshmarker.core.fragment;

import java.util.Map;
import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.model.TemplateObject;

public class UserDirectiveFragment implements Fragment {
  private final String directive;
  private final Map<String, TemplateObject> namedArgs;
  private final BlockFragment body;

  public UserDirectiveFragment(String directive, Map<String, TemplateObject> namedArgs, BlockFragment body) {
    this.directive = directive;
    this.namedArgs = namedArgs;
    this.body = body;
  }

  @Override
  public void process(ProcessContext context) {
    context.getEnvironment().getDirective(directive).execute(context, namedArgs, body);
  }
}
