package org.freshmarker.core.fragment;

import java.util.ArrayList;
import java.util.List;
import org.freshmarker.core.ProcessContext;

public class BlockFragment implements Fragment {

  private final List<Fragment> fragments = new ArrayList<>();

  public void addFragment(Fragment fragment) {
    fragments.add(fragment);
  }

  @Override
  public void process(ProcessContext context) {
    fragments.forEach(f -> f.process(context));
  }
}
