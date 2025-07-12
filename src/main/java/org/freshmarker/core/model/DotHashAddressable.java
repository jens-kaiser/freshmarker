package org.freshmarker.core.model;

import org.freshmarker.core.ProcessContext;

public interface DotHashAddressable {

    TemplateObject get(ProcessContext context, String name);
}
