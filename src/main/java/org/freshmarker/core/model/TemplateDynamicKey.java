package org.freshmarker.core.model;

import org.freshmarker.core.Environment;
import org.freshmarker.core.ProcessException;
import org.freshmarker.core.model.primitive.TemplateNumber;
import org.freshmarker.core.model.primitive.TemplateObject;

public class TemplateDynamicKey implements TemplateExpression {

  private final TemplateObject sequence;
  private final TemplateObject dynamicKey;

  public TemplateDynamicKey(TemplateObject sequence, TemplateObject dynamicKey) {
    this.sequence = sequence;
    this.dynamicKey = dynamicKey;
  }

  @Override
  public TemplateObject evaluateToObject(Environment environment) {
    TemplateObject index = dynamicKey.evaluateToObject(environment);
    TemplateNumber indexValue = index.asNumber()
        .orElseThrow(() -> new ProcessException("index is not a number: " + index));
    TemplateObject templateObject = sequence.evaluateToObject(environment);
    if (templateObject instanceof TemplateRange) {
      TemplateRange range = (TemplateRange) templateObject;
      TemplateObject lower = range.getLower().evaluateToObject(environment);
      TemplateNumber lowerNumber = lower.asNumber()
          .orElseThrow(() -> new ProcessException("lower limit is not a number: " + lower));
      TemplateNumber result = lowerNumber.add(indexValue);
      if (range.isRightUnlimited()) {
        return result;
      }
      TemplateObject upper = range.getUpper().evaluateToObject(environment);
      TemplateNumber upperNumber = lower.asNumber()
          .orElseThrow(() -> new ProcessException("upper limit is not a number: " + upper));
      if (result.compare(upperNumber).getValue().intValue() < 0) {
        return result;
      }
      throw new ProcessException("index out of range");
    }
    TemplateListSequence list = (TemplateListSequence) templateObject;
    return list.get(environment, indexValue.getValue().intValue());
  }
}
