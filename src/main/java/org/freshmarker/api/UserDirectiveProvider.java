package org.freshmarker.api;

import org.freshmarker.core.directive.UserDirective;

import java.util.Map;

public interface UserDirectiveProvider extends Extension {
    Map<String, UserDirective> provideUserDirectives();
}
