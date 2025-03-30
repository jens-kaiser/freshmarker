package org.freshmarker.api;

import org.freshmarker.core.directive.UserDirective;

import java.util.Map;

/**
 * An {@link Extension} to add new user directives.
 */
public interface UserDirectiveProvider extends Extension {
    /**
     * Returns a map of user directives
     * @return a map of user directives
     */
    Map<String, UserDirective> provideUserDirectives();
}
