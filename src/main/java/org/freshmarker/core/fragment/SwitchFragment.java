package org.freshmarker.core.fragment;

import ftl.Node;
import java.util.ArrayList;
import java.util.List;
import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.UnsupportedBuiltInException;
import org.freshmarker.core.WrongTypeException;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.model.primitive.TemplatePrimitive;

public class SwitchFragment implements Fragment {

  private final List<ConditionalFragment> fragments = new ArrayList<>();
  private Fragment defaultFragment;

  private final TemplateObject switchExpression;
  private final Node node;

  public SwitchFragment(TemplateObject switchExpression, Node node) {
    this.switchExpression = switchExpression;
    this.node = node;
  }

  public void addFragment(ConditionalFragment fragment) {
    fragments.add(fragment);
  }

  public void addDefaultFragment(Fragment fragment) {
    defaultFragment = fragment;
  }

  public void process(ProcessContext context) {
    TemplatePrimitive<?> switchValue = evaluatePrimitive(this.switchExpression, context, node);
    for (ConditionalFragment fragment : fragments) {
      if (switchValue.equals(evaluatePrimitive(fragment.getConditional(), context, fragment.getNode()))) {
        fragment.process(context);
        return;
      }
    }
    defaultFragment.process(context);
  }

  private TemplatePrimitive<?> evaluatePrimitive(TemplateObject conditional, ProcessContext context, Node node) {
    try {
      return conditional.evaluateToObject(context).asPrimitive()
          .orElseThrow(() -> new WrongTypeException("not a primitive type", node));
    } catch (UnsupportedBuiltInException e) {
      throw new UnsupportedBuiltInException(e.getMessage(), node, e);
    }catch (WrongTypeException e) {
      throw new WrongTypeException(e.getMessage(), node, e);
    }
  }
}
