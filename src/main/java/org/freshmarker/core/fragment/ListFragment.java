package org.freshmarker.core.fragment;

import org.freshmarker.core.Environment;
import org.freshmarker.core.environment.ListEnvironment;
import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.ProcessException;
import org.freshmarker.core.environment.VariableEnvironment;
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
    Environment environment = context.getEnvironment();
    Environment listEnvironment = new VariableEnvironment(new ListEnvironment(context.getEnvironment(), identifier, looper));
    context.setEnvironment(listEnvironment);
    for (int i = 0; i < size; i++) {
      block.process(context);
      looper.increment();
    }
    context.setEnvironment(environment);
  }
}
