package org.freshmarker.core.buildin;

import java.util.List;
import org.freshmarker.core.Environment;
import org.freshmarker.core.model.primitive.TemplateObject;

public interface BuildInFunction {
  TemplateObject apply(TemplateObject value, List<TemplateObject> parameter, Environment environment);
}
