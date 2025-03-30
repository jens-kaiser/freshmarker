package org.freshmarker.api;

import org.freshmarker.core.BuiltInVariable;

import java.util.Map;

/**
 * An {@link Extension} to add new built-tns variables.
 */
public interface BuiltInVariableProvider extends Extension{
    /**
     * Returns a map of built-in variables
     * @return a map of built-in variables
     */
    Map<String, BuiltInVariable> provideBuiltInVariables();
}
