package org.freshmarker.api;

import org.freshmarker.core.directive.TemplateFunction;

public interface NamedFunction extends TemplateFunction, Extension {
    String name();
}
