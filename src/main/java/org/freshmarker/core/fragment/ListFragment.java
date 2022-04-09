package org.freshmarker.core.fragment;

import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import org.freshmarker.core.Environment;
import org.freshmarker.core.ListEnvironent;
import org.freshmarker.core.ProcessException;
import org.freshmarker.core.model.TemplateLooper;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.model.TemplateSequence;
import org.freshmarker.core.model.primitive.TemplateNumber;

public class ListFragment implements Fragment {

  private final TemplateObject list;
  private final String identifier;
  private final BlockFragment block;

  public ListFragment(TemplateObject list, String identifier, BlockFragment block) {
    this.list = list;
    this.identifier = identifier;
    this.block = block;
  }

  @Override
  public void process(Environment environment, Writer writer) {
    TemplateSequence sequence = (TemplateSequence) list.evaluateToObject(environment);
    int size = sequence.size(environment).asNumber().map(TemplateNumber::asInt)
        .orElseThrow(() -> new ProcessException("no number"));
    TemplateLooper looper = new TemplateLooper(sequence, size);
    ListEnvironent listEnvironment = new ListEnvironent(environment, identifier, looper);
    for (int i = 0; i < size; i++) {
      block.process(listEnvironment, writer);
      looper.increment();
    }
  }
}
