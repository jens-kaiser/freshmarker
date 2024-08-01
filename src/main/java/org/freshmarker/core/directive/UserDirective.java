package org.freshmarker.core.directive;

import java.util.Map;
import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.fragment.Fragment;
import org.freshmarker.core.model.TemplateObject;

public interface UserDirective {

  void execute(ProcessContext context, Map<String, TemplateObject> args, Fragment body);
}
