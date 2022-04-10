package org.freshmarker;

import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;
import org.freshmarker.core.fragment.BlockFragment;

public final class Template {

  private final BlockFragment rootFragment = new BlockFragment();
  private final Configuration configuration;

  public Template(Configuration configuration) {
    this.configuration = configuration;
  }

  public void process(Map<String, Object> dataModel, Writer writer) {
    rootFragment.process(configuration.createContext(dataModel, writer));
  }

  public String process(Map<String, Object> dataModel) {
    StringWriter writer = new StringWriter();
    process(dataModel, writer);
    return writer.toString();
  }

  public BlockFragment getRootFragment() {
    return rootFragment;
  }
}
