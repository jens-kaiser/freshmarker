package org.freshmarker.core.fragment;

import java.io.IOException;
import java.io.Writer;
import org.freshmarker.core.Environment;
import org.freshmarker.core.ProcessException;

public class ConstantFragment implements Fragment {

  private final String value;

  public ConstantFragment(String value) {
    this.value = value;
  }

  @Override
  public void process(Environment environment, Writer writer) {
    try {
      writer.write(value);
    } catch (IOException e) {
      throw new ProcessException(e.getMessage(), e);
    }
  }
}
