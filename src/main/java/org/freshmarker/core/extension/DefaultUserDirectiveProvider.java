package org.freshmarker.core.extension;

import org.freshmarker.api.extension.UserDirectiveProvider;
import org.freshmarker.core.directive.CompressDirective;
import org.freshmarker.core.directive.OneLinerDirective;
import org.freshmarker.core.directive.UserDirective;

import java.util.Map;

public class DefaultUserDirectiveProvider implements UserDirectiveProvider {
    @Override
    public Map<String, UserDirective> provideUserDirectives() {
        return Map.of("compress", new CompressDirective(), "oneliner", new OneLinerDirective());
    }
}
