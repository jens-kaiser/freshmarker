package org.freshmarker.core.model;

import org.freshmarker.core.Environment;
import org.freshmarker.core.model.primitive.TemplateNumber;

public class TemplateSlice implements TemplateObject {

  private final TemplateObject sequence;
  private final TemplateObject range;

  public TemplateSlice(TemplateObject sequence, TemplateObject range) {
    this.sequence = sequence;
    this.range = range;
  }

  @Override
  public TemplateListSequence evaluateToObject(Environment environment) {
    TemplateRange templateRange = (TemplateRange) range.evaluateToObject(environment);
    TemplateListSequence templateListSequence = (TemplateListSequence) sequence.evaluateToObject(environment);
    TemplateNumber lower = (TemplateNumber) templateRange.getLower().evaluateToObject(environment);
    int min = lower.getValue().intValue();
    if (templateRange.isRightUnlimited()) {
      return templateListSequence.slice(min, templateListSequence.size(environment).getValue().intValue());
    }
    TemplateNumber upper = (TemplateNumber) templateRange.getUpper().evaluateToObject(environment);
    int max = upper.getValue().intValue();
    if (templateRange.isLengthLimited()) {
      return templateListSequence.slice(min, Math.max(templateListSequence.size(environment).getValue().intValue(), min + max));
    }
    return templateListSequence.slice(min, max);
  }
}
