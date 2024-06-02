package org.freshmarker.core.model;

import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.model.primitive.TemplateNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.AbstractList;
import java.util.List;

public class TemplateRightLimitedRange implements TemplateRange {

  private static final Logger logger = LoggerFactory.getLogger(TemplateRightLimitedRange.class);

  private final TemplateObject lower;
  private final TemplateObject upper;

  private int lowerNumber;
  private int upperNumber;

  public TemplateRightLimitedRange(TemplateObject lower, TemplateObject upper) {
    this.lower = lower;
    this.upper = upper;
  }

  @Override
  public TemplateObject evaluateToObject(ProcessContext context) {
    return this;
  }

  @Override
  public boolean isLengthLimited() {
    return false;
  }

  @Override
  public boolean isRightUnlimited() {
    return false;
  }

  @Override
  public TemplateObject getLower() {
    return lower;
  }

  @Override
  public TemplateObject getUpper() {
    return upper;
  }

  @Override
  public TemplateObject get(ProcessContext context, int index) {
    logger.debug("get: {}", index);
    evaluate(context);
    return new TemplateNumber(lowerNumber < upperNumber ? lowerNumber  + index : lowerNumber - index);
  }

  @Override
  public TemplateNumber size(ProcessContext context) {
    evaluate(context);
    int size = Math.abs(upperNumber - lowerNumber) + 1;
    logger.debug("size: {}", size);
    return new TemplateNumber(size);
  }

  @Override
  public List<Object> getSequence(ProcessContext context) {
    evaluate(context);
    int size = Math.abs(upperNumber - lowerNumber) + 1;
    return new AbstractList<>() {
      @Override
      public Object get(int index) {
        return lowerNumber < upperNumber ? lowerNumber + index : lowerNumber - index;
      }

      @Override
      public int size() {
        return size;
      }
    };
  }

  private void evaluate(ProcessContext context) {
    lowerNumber = lowerNumber != 0 ? lowerNumber : lower.evaluate(context, TemplateNumber.class).asInt();
    upperNumber = upperNumber != 0 ? upperNumber : upper.evaluate(context, TemplateNumber.class).asInt();
  }
}
