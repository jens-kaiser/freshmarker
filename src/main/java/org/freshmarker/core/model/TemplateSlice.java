package org.freshmarker.core.model;

import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.UnsupportedDataTypeException;
import org.freshmarker.core.model.primitive.TemplateNumber;
import org.freshmarker.core.model.primitive.TemplateString;

public class TemplateSlice implements TemplateObject {

  private final TemplateObject sequence;
  private final TemplateObject range;

  public TemplateSlice(TemplateObject sequence, TemplateObject range) {
    this.sequence = sequence;
    this.range = range;
  }

  @Override
  public TemplateObject evaluateToObject(ProcessContext context) {
    TemplateRange templateRange = range.evaluate(context, TemplateRange.class);
    TemplateObject value = sequence.evaluateToObject(context);
    if (value instanceof TemplateString) {
      return handleSequence(context, templateRange, (TemplateString) value);

    } else if (value instanceof TemplateListSequence) {
      return handleSequence(context, templateRange, (TemplateListSequence) value);
    }
    throw new UnsupportedDataTypeException("slicing not supported on " + value.getClass().getSimpleName());
  }

  private TemplateListSequence handleSequence(ProcessContext context, TemplateRange templateRange,
      TemplateListSequence templateListSequence) {
    TemplateNumber lower = templateRange.getLower().evaluate(context, TemplateNumber.class);
    int min = lower.getValue().getNumber().intValue();
    if (templateRange.isRightUnlimited()) {
      return templateListSequence.slice(min);
    }
    TemplateNumber upper = templateRange.getUpper().evaluate(context, TemplateNumber.class);
    int max = upper.getValue().getNumber().intValue();
    if (templateRange.isLengthLimited()) {
      return templateListSequence.slice(min,
          Math.max(templateListSequence.size(context).getValue().getNumber().intValue(), min + max));
    }
    return templateListSequence.slice(min, max);
  }

  private TemplateString handleSequence(ProcessContext context, TemplateRange templateRange,
      TemplateString templateString) {
    TemplateNumber lower = templateRange.getLower().evaluate(context, TemplateNumber.class);
    String value = templateString.getValue();
    int min = lower.getValue().getNumber().intValue();
    if (templateRange.isRightUnlimited()) {
      return new TemplateString(value.substring(min));
    }
    TemplateNumber upper = templateRange.getUpper().evaluate(context, TemplateNumber.class);
    int max = upper.getValue().getNumber().intValue() + 1;
    if (templateRange.isLengthLimited()) {
      return new TemplateString(value.substring(min, Math.max(value.length(), min + max)));
    }
    return new TemplateString(value.substring(min, max));
  }
}
