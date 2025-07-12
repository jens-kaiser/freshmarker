package org.freshmarker.core.model;

import java.util.Map;

public interface TemplateMap extends TemplateObject, DotHashAddressable {

    Map<String, Object> map();
}
