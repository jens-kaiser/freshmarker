package org.freshmarker.core;

import java.io.Writer;

public class WriterEnvironment extends WrapperEnvironment {

  private final Writer writer;

  public WriterEnvironment(Writer writer, Environment environment) {
    super(environment);
    this.writer = writer;
  }

  @Override
  public Writer getWriter() {
    return writer;
  }
}
