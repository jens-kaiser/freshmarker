package org.freshmarker.api;

import org.freshmarker.core.directive.TemplateFunction;

import java.util.Map;

public interface FunctionProvider extends Extension {
    Map<String, TemplateFunction> provideFunctions();
}
