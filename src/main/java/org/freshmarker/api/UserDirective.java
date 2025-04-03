package org.freshmarker.api.extension;

import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.fragment.Fragment;
import org.freshmarker.core.model.TemplateObject;

import java.util.Map;

public interface UserDirective {

  void execute(ProcessContext context, Map<String, TemplateObject> args, Fragment body);
}