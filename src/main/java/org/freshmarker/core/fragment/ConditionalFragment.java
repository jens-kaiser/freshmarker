package org.freshmarker.core.fragment;

import ftl.Node;
import java.util.Objects;
import org.freshmarker.core.ProcessContext;
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
    content.process(context);
  }
}
