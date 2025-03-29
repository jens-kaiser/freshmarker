package org.freshmarker.api;

import org.freshmarker.core.BuiltInVariable;

import java.util.Map;

public interface BuiltInVariableProvider extends Extension{
    Map<String, BuiltInVariable> provideBuiltInVariables();
}
