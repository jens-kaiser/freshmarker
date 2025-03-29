package org.freshmarker.core.extension;

import org.freshmarker.api.NamedUserDirective;
import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.directive.UserDirective;
import org.freshmarker.core.fragment.Fragment;
import org.freshmarker.core.model.TemplateObject;

import java.util.Map;

public class UserDirectiveAdapter implements NamedUserDirective {
    private final String name;
    private final UserDirective userDirective;

    public UserDirectiveAdapter(String name, UserDirective userDirective) {
        this.name = name;
        this.userDirective = userDirective;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public void execute(ProcessContext context, Map<String, TemplateObject> args, Fragment body) {
        userDirective.execute(context, args, body);
    }
}
