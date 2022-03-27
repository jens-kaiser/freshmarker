package org.freshmarker.core.fragment;

import java.io.Writer;
import org.freshmarker.core.Environment;

public interface Fragment {

  void process(Environment environment, Writer writer);
}
