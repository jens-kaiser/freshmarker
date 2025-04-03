package org.freshmarker.api.extension;

import org.freshmarker.api.UserDirective;

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
