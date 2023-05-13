package org.freshmarker.core.model;

import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.ProcessException;
import org.freshmarker.core.model.primitive.TemplateNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class TemplateRightUnlimitedRange implements TemplateRange {

  private static final Logger logger = LoggerFactory.getLogger(TemplateRightUnlimitedRange.class);

  private final TemplateObject lower;

  private int lowerNumber;

  public TemplateRightUnlimitedRange(TemplateObject lower) {
    this.lower = lower;
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
    return true;
  }

  @Override
  public TemplateObject getLower() {
    return lower;
  }

  @Override
  public TemplateObject getUpper() {
    return TemplateNull.NULL;
  }

  @Override
  public TemplateObject get(ProcessContext context, int index) {
    logger.info("get: {}", index);
    lowerNumber = lowerNumber != 0 ? lowerNumber : lower.evaluate(context, TemplateNumber.class).asInt();
    return new TemplateNumber(lowerNumber  + index);
  }

  @Override
  public TemplateNumber size(ProcessContext context) {
      throw new ProcessException("right unlimited range not supported");
  }

  @Override
  public List<Object> getSequence(ProcessContext context) {
      throw new ProcessException("right unlimited range not supported");
  }
}
