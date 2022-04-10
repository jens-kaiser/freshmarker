package org.freshmarker.core.model;

import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.model.primitive.TemplateNumber;

public class TemplateSlice implements TemplateObject {

  private final TemplateObject sequence;
  private final TemplateObject range;

  public TemplateSlice(TemplateObject sequence, TemplateObject range) {
    this.sequence = sequence;
    this.range = range;
  }

  @Override
  public TemplateListSequence evaluateToObject(ProcessContext context) {
    TemplateRange templateRange = range.evaluate(context, TemplateRange.class);
    TemplateListSequence templateListSequence = sequence.evaluate(context, TemplateListSequence.class);
    TemplateNumber lower = templateRange.getLower().evaluate(context, TemplateNumber.class);
    int min = lower.getValue().intValue();
    if (templateRange.isRightUnlimited()) {
      return templateListSequence.slice(min, templateListSequence.size(context).getValue().intValue());
    }
    TemplateNumber upper = templateRange.getUpper().evaluate(context, TemplateNumber.class);
    int max = upper.getValue().intValue();
    if (templateRange.isLengthLimited()) {
      return templateListSequence.slice(min, Math.max(templateListSequence.size(context).getValue().intValue(), min + max));
    }
    return templateListSequence.slice(min, max);
  }
}
