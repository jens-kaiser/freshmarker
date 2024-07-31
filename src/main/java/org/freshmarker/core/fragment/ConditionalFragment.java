package org.freshmarker.core.fragment;

import ftl.Node;
import java.util.Objects;

import org.freshmarker.core.Environment;
import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.ReduceContext;
import org.freshmarker.core.environment.VariableEnvironment;
import org.freshmarker.core.model.TemplateObject;

public class ConditionalFragment implements Fragment {

  private final TemplateObject conditional;
  private final BlockFragment content;
  private final Node node;

  public ConditionalFragment(TemplateObject conditional, BlockFragment content, Node node) {
    this.conditional = Objects.requireNonNull(conditional);
    this.content = Objects.requireNonNull(content);
    this.node = node;
  }

  public Node getNode() {
    return node;
  }

  public TemplateObject getConditional() {
    return conditional;
  }

  @Override
  public void process(ProcessContext context) {
    Environment environment = context.getEnvironment();
    try {
      context.setEnvironment(new VariableEnvironment(environment));
      content.process(context);
    } finally {
      context.setEnvironment(environment);
    }
  }

  @Override
  public ConditionalFragment reduce(ReduceContext context) {
    return new ConditionalFragment(conditional, content.reduce(context), node);
  }

  @Override
  public int getSize() {
    return content.getSize() + 1;
  }
}
