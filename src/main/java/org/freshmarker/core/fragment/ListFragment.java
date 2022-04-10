package org.freshmarker.core.fragment;

import org.freshmarker.core.ListEnvironent;
import org.freshmarker.core.ProcessContext;
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
  public void process(ProcessContext context) {
    TemplateSequence sequence = (TemplateSequence) list.evaluateToObject(context);
    int size = sequence.size(context).asNumber().map(TemplateNumber::asInt)
        .orElseThrow(() -> new ProcessException("no number"));
    TemplateLooper looper = new TemplateLooper(sequence, size);
    ListEnvironent listEnvironment = new ListEnvironent(context.getEnvironment(), identifier, looper);
    ProcessContext listContext = new ProcessContext(listEnvironment, context.getWriter());
    for (int i = 0; i < size; i++) {
      block.process(listContext);
      looper.increment();
    }
  }
}
