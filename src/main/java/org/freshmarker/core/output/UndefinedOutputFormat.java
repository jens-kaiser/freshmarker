package org.freshmarker.core.output;

public class UndefinedOutputFormat implements OutputFormat {

  public static final UndefinedOutputFormat INSTANCE = new UndefinedOutputFormat();

  private UndefinedOutputFormat() {
    super();
  }
}
