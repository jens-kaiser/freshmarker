package org.freshmarker.core.fragment;

import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.model.TemplateObject;

public class ConditionalFragment implements Fragment {

  private final TemplateObject conditional;
  private final BlockFragment content;

  public ConditionalFragment(TemplateObject conditional, BlockFragment content) {
    this.conditional = conditional;
    this.content = content;
  }

  public TemplateObject getConditional() {
    return conditional;
  }

  @Override
  public void process(ProcessContext context) {
    content.process(context);
  }
}
