package org.freshmarker.api;

import org.freshmarker.core.buildin.BuiltIn;
import org.freshmarker.core.buildin.BuiltInKey;

import java.util.Map;

public interface BuiltInProvider extends Extension {
    Map<BuiltInKey, BuiltIn> provideBuiltIns();
}
