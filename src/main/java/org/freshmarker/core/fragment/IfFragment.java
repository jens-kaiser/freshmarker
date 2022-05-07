package org.freshmarker.core.fragment;

import java.util.ArrayList;
import java.util.List;
import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.UnsupportedBuiltInException;
import org.freshmarker.core.model.primitive.TemplateBoolean;

public class IfFragment implements Fragment {

  private final List<ConditionalFragment> fragments = new ArrayList<>();

  public void addFragment(ConditionalFragment fragment) {
    fragments.add(fragment);
  }

  @Override
  public void process(ProcessContext context) {
    fragments.stream()
        .filter(f -> filterByConditional(context, f))
        .findFirst().ifPresent(f -> f.process(context));
  }

  private boolean filterByConditional(ProcessContext context, ConditionalFragment conditionalFragment) {
    try {
      return conditionalFragment.getConditional().evaluate(context, TemplateBoolean.class) == TemplateBoolean.TRUE;
    } catch (UnsupportedBuiltInException e) {
      throw new UnsupportedBuiltInException(e.getMessage(), conditionalFragment.getNode(), e);
    }
  }
}
