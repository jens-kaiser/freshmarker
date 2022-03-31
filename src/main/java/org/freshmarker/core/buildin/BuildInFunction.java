package org.freshmarker.core.buildin;

import java.util.List;
import org.freshmarker.core.Environment;
import org.freshmarker.core.model.TemplateObject;

public interface BuildInFunction {
  TemplateObject apply(TemplateObject value, List<TemplateObject> parameter, Environment environment);
}
