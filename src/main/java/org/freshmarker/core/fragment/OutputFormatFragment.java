package org.freshmarker.core.fragment;

import org.freshmarker.core.Environment;
import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.SettingEnvironment;

public class OutputFormatFragment implements Fragment {

  private final BlockFragment content;
  private final String format;

  public OutputFormatFragment(BlockFragment content, String format) {
    this.content = content;
    this.format = format;
  }

  @Override
  public void process(ProcessContext context) {
    Environment environment = context.getEnvironment();
    context.setEnvironment(new SettingEnvironment(environment, null, context.getOutputFormat(format)));
    content.process(context);
    context.setEnvironment(environment);
  }
}
