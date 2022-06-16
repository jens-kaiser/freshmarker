package org.freshmarker.core.providers;

import org.freshmarker.core.Environment;
import org.freshmarker.core.model.TemplateBean;
import org.freshmarker.core.model.TemplateBeanProvider;
import org.freshmarker.core.model.TemplateObject;

public class BeanTemplateObjectProvider implements TemplateObjectProvider {

  private final TemplateBeanProvider beanProvider = new TemplateBeanProvider();

  @Override
  public TemplateObject provide(Environment environment, Object o) {
    if (!o.getClass().isPrimitive() && !o.getClass().getName().startsWith("java")) {
      return new TemplateBean(beanProvider.provide(o, environment));
    }
    return null;
  }
}
