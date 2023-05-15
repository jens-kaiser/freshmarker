package org.freshmarker.core.fragment;

import ftl.Node;
import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.ProcessException;
import org.freshmarker.core.model.TemplateNull;
import org.freshmarker.core.model.TemplateObject;

public class VariableFragment implements Fragment {

  private final String name;
  private final TemplateObject expression;

  private final boolean exists;

  private final Node node;

  public VariableFragment(String name, TemplateObject expression, boolean exists, Node node) {
    this.name = name;
    this.expression = expression;
    this.exists = exists;
    this.node = node;
  }

  @Override
  public void process(ProcessContext context) {
    TemplateObject value = context.getEnvironment().getValue(name);
    if (exists == (value == TemplateNull.NULL)) {
      throw new ProcessException("variable " + name + (exists ? " already exists" : " does not exists"), node);
    }
    context.getEnvironment().setVariable(name, expression.evaluateToObject(context));
  }
}
