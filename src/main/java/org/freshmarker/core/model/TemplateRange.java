package org.freshmarker.core.model;

import org.freshmarker.core.Environment;

public class TemplateRange implements TemplateObject {

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
}
