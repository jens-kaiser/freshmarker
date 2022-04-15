package org.freshmarker.core.model;

import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.ProcessException;
import org.freshmarker.core.model.primitive.TemplateNumber;

public class TemplateDynamicKey implements TemplateExpression {

  private final TemplateObject sequence;
  private final TemplateObject dynamicKey;

  public TemplateDynamicKey(TemplateObject sequence, TemplateObject dynamicKey) {
    this.sequence = sequence;
    this.dynamicKey = dynamicKey;
  }

  @Override
  public TemplateObject evaluateToObject(ProcessContext context) {
    TemplateNumber index = dynamicKey.evaluate(context, TemplateNumber.class);
    TemplateObject templateObject = sequence.evaluateToObject(context);
    if (templateObject instanceof TemplateRange) {
      TemplateRange range = (TemplateRange) templateObject;
      TemplateNumber lower = range.getLower().evaluate(context, TemplateNumber.class);
      TemplateNumber result = lower.add(index);
      if (range.isRightUnlimited()) {
        return result;
      }
      TemplateNumber upper = range.getUpper().evaluate(context, TemplateNumber.class);
      if (result.compare(upper).getValue().getNumber().intValue() < 0) {
        return result;
      }
      throw new ProcessException("index out of range: " + result.getValue() + " " + upper.getValue());
    }
    TemplateListSequence list = (TemplateListSequence) templateObject;
    return list.get(context, index.getValue().getNumber().intValue());
  }
}
