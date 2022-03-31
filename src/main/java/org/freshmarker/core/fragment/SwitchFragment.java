package org.freshmarker.core.fragment;

import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import org.freshmarker.core.Environment;
import org.freshmarker.core.ProcessException;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.model.primitive.TemplatePrimitive;

public class SwitchFragment implements Fragment {

  private final List<ConditionalFragment> fragments = new ArrayList<>();
  private Fragment defaultFragment;

  private final TemplateObject switchExpression;

  public SwitchFragment(TemplateObject switchExpression) {
    this.switchExpression = switchExpression;
  }

  public void addFragment(ConditionalFragment fragment) {
    fragments.add(fragment);
  }
  public void addDefaultFragment(Fragment fragment) {
    defaultFragment = fragment;
  }

  public void process(Environment environment, Writer writer) {
    TemplatePrimitive<?> switchValue = switchExpression.evaluateToObject(environment).asPrimitive()
        .orElseThrow(() -> new ProcessException("not a primitive type"));
    for (ConditionalFragment fragment : fragments) {
      TemplatePrimitive<?> conditionalValue = fragment.getConditional().evaluateToObject(environment).asPrimitive()
          .orElse(null);
      if (switchValue.equals(conditionalValue)) {
        fragment.process(environment, writer);
        return;
      }
    }
    defaultFragment.process(environment, writer);
  }
}
