package org.freshmarker.core.model;

import org.freshmarker.core.ProcessContext;

public interface DotAddressable {

    TemplateObject get(ProcessContext context, String name);
}
