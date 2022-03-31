package org.freshmarker.core.model;

import java.util.List;
import org.freshmarker.core.Environment;
import org.freshmarker.core.model.primitive.TemplateNumber;
import org.freshmarker.core.model.primitive.TemplateNumber.Type;

public class TemplateListSequence implements TemplateSequence {

  private final List<Object> sequence;

  public TemplateListSequence(List<Object> sequence) {
    this.sequence = sequence;
  }

  @Override
  public TemplateObject get(Environment environment, int index) {
    return environment.mapObject(sequence.get(index));
  }

  @Override
  public TemplateNumber size(Environment environment) {
    return new TemplateNumber(sequence.size(), Type.INTEGER);
  }

  public TemplateListSequence slice(int min, int max) {
    return new TemplateListSequence(sequence.subList(min, max));
  }

  @Override
  public TemplateObject evaluateToObject(Environment environment) {
    return this;
  }
}
