package org.freshmarker.core.model;

import java.util.Map;

public interface TemplateMap extends TemplateObject, DotAddressable {

    Map<String, Object> map();
}
