package org.freshmarker.core.fragment;

import org.freshmarker.core.ProcessContext;

public class ReturnInstructionFragment implements Fragment {

  @Override
  public void process(ProcessContext context) {
    throw new TemplateReturnException();
  }

  @Override
  public void accept(TemplateVisitor visitor) {
    visitor.visit(this);
  }
}
