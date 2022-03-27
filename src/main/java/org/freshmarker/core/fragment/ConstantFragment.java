package org.freshmarker.core.fragment;

import java.io.IOException;
import java.io.Writer;
import org.freshmarker.core.Environment;
import org.freshmarker.core.ProcessException;
import org.freshmarker.core.model.primitive.TemplatePrimitive;

public class ConstantFragment<T extends TemplatePrimitive<?>> implements Fragment {

  private final T value;

  public ConstantFragment(T value) {
    this.value = value;
  }

  @Override
  public void process(Environment environment, Writer writer) {
    try {
      writer.write(value.evaluate(environment));
    } catch (IOException e) {
      throw new ProcessException(e.getMessage(), e);
    }
  }
}
