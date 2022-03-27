package org.freshmarker.core.fragment;

import java.io.Writer;
import org.freshmarker.core.Environment;

public class EmptyFragment implements Fragment {

  public static final EmptyFragment EMPTY = new EmptyFragment();

  @Override
  public void process(Environment environment, Writer writer) {

  }
}
