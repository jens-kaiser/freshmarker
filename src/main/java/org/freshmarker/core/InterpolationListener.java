package org.freshmarker.core;

import org.freshmarker.core.model.TemplateObject;

public interface InterpolationListener {
 default void evaluatedInterpolation(String formular, TemplateObject expression) {

 }
}
