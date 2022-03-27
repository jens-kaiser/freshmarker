package org.freshmarker.core.fragment;

import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import org.freshmarker.core.Environment;

public class BlockFragment implements Fragment {

  private final List<Fragment> fragments = new ArrayList<>();

  public void addFragment(Fragment fragment) {
    fragments.add(fragment);
  }

  public void process(Environment environment, Writer writer) {
    fragments.forEach(f -> f.process(environment, writer));
  }
}
