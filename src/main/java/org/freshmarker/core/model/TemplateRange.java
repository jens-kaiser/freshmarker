package org.freshmarker.core.model;

import org.freshmarker.core.Environment;
import org.freshmarker.core.ProcessException;
import org.freshmarker.core.model.primitive.TemplateNumber;
import org.freshmarker.core.model.primitive.TemplateNumber.Type;
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
    int lowerNumber = lower.evaluate(environment, TemplateNumber.class).asInt();
    return new TemplateNumber(lowerNumber + index, Type.INTEGER);
  }

  @Override
  public TemplateNumber size(Environment environment) {
    if (rightUnlimited) {
      throw new ProcessException("right unlimited range not supported");
    }
    int lowerValue = lower.evaluate(environment, TemplateNumber.class).asInt();
    int upperValue = upper.evaluate(environment, TemplateNumber.class).asInt();
    int size = Math.abs(upperValue - lowerValue) + 1;
    logger.info("size: {}", size);
    return new TemplateNumber(size, Type.INTEGER);
  }
}
