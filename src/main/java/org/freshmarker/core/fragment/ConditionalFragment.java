package org.freshmarker.core.fragment;

import java.io.Writer;
import org.freshmarker.core.Environment;
import org.freshmarker.core.model.primitive.TemplateObject;

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
  public void process(Environment environment, Writer writer) {
    content.process(environment, writer);
  }
}
