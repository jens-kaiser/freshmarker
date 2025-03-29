package org.freshmarker.api;

import org.freshmarker.core.directive.UserDirective;

public interface NamedUserDirective extends UserDirective, Extension {
    String name();
}
