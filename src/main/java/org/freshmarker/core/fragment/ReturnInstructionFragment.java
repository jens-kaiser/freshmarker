package org.freshmarker.core.fragment;

import org.freshmarker.core.ProcessContext;

public class ReturnInstructionFragment implements Fragment {

  @Override
  public void process(ProcessContext context) {
    throw new TemplateReturnException();
  }

  @Override
  public <R> R accept(TemplateVisitor<R> visitor) {
    return visitor.visit(this);
  }
}
