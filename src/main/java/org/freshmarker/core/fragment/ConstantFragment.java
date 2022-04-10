package org.freshmarker.core.fragment;

import java.io.IOException;
import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.ProcessException;

public class ConstantFragment implements Fragment {

  public static final ConstantFragment EMPTY = new ConstantFragment("");

  private final String value;

  public ConstantFragment(String value) {
    this.value = value;
  }

  @Override
  public void process(ProcessContext context) {
    try {
      context.getWriter().write(value);
    } catch (IOException e) {
      throw new ProcessException(e.getMessage(), e);
    }
  }
}
