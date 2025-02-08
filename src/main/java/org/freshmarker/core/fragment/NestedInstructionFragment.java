package org.freshmarker.core.fragment;

import org.freshmarker.core.ProcessContext;

public class NestedInstructionFragment implements Fragment {

  @Override
  public void process(ProcessContext context) {
    context.getEnvironment().getNestedContent().ifPresent(n -> n.process(context));
  }

  @Override
  public <R> R accept(TemplateVisitor<R> visitor) {
    return visitor.visit(this);
  }
}
