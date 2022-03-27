package org.freshmarker.core.fragment;

import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import org.freshmarker.core.Environment;
import org.freshmarker.core.model.primitive.TemplateBoolean;

public class IfFragment implements Fragment {

  private final List<ConditionalFragment> fragments = new ArrayList<>();

  public void addFragment(ConditionalFragment fragment) {
    fragments.add(fragment);
  }

  public void process(Environment environment, Writer writer) {
    for (ConditionalFragment fragment : fragments) {
      if (fragment.getConditional().evaluateToObject(environment) == TemplateBoolean.TRUE) {
        fragment.process(environment, writer);
        return;
      }
    }
  }
}
