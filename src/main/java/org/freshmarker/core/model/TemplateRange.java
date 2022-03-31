package org.freshmarker.core.model;

import org.freshmarker.core.Environment;
import org.freshmarker.core.ProcessException;
import org.freshmarker.core.model.primitive.TemplateNumber;
import org.freshmarker.core.model.primitive.TemplateNumber.Type;
import org.freshmarker.core.model.primitive.TemplatePrimitive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TemplateRange implements TemplateSequence {

  private static final Logger logger = LoggerFactory.getLogger(TemplateRange.class);

  private final boolean lengthLimited;
  private final boolean rightUnlimited;
  private final TemplateObject lower;
  private final TemplateObject upper;

  public TemplateRange(boolean lengthLimited, boolean rightUnlimited, TemplateObject lower, TemplateObject upper) {
    this.lengthLimited = lengthLimited;
    this.rightUnlimited = rightUnlimited;
    this.lower = lower;
    this.upper = upper;
  }

  @Override
  public TemplateObject evaluateToObject(Environment environment) {
    return this;
  }

  public boolean isLengthLimited() {
    return lengthLimited;
  }

  public boolean isRightUnlimited() {
    return rightUnlimited;
  }

  public TemplateObject getLower() {
    return lower;
  }

  public TemplateObject getUpper() {
    return upper;
  }

  @Override
  public TemplateObject get(Environment environment, int index) {
    logger.info("get: {}", index);
    TemplateObject lowerValue = lower.evaluateToObject(environment);
    Number lowerNumber = lowerValue.asNumber().map(TemplatePrimitive::getValue)
        .orElseThrow(() -> new ProcessException("no number: " + lowerValue));
    return new TemplateNumber(lowerNumber.intValue() + index, Type.INTEGER);
  }

  @Override
  public TemplateNumber size(Environment environment) {
    if (rightUnlimited) {
      throw new ProcessException("right unlimited range not supported");
    }
    TemplateNumber lowerValue = lower.evaluateToObject(environment).asNumber()
        .orElseThrow(() -> new ProcessException("no number"));
    TemplateNumber upperValue = upper.evaluateToObject(environment).asNumber()
        .orElseThrow(() -> new ProcessException("no number"));

    int size = Math.abs(upperValue.getValue().intValue() - lowerValue.getValue().intValue()) + 1;
    logger.info("size: {}", size);
    return new TemplateNumber(size, Type.INTEGER);
  }
}
