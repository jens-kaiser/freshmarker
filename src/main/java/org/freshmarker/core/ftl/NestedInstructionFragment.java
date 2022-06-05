package org.freshmarker.core.ftl;

import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.fragment.Fragment;

public class NestedInstructionFragment implements Fragment {

  @Override
  public void process(ProcessContext context) {
    context.getEnvironment().getNestedContent().ifPresent(n -> n.process(context));
  }
}
