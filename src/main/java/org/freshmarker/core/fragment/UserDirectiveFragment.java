package org.freshmarker.core.fragment;

import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.model.TemplateObject;

import java.util.Map;

public class UserDirectiveFragment implements Fragment {
    private final String directive;
    private final String nameSpace;
    private final Map<String, TemplateObject> namedArgs;
    private final Fragment body;

    public UserDirectiveFragment(String directive, String nameSpace, Map<String, TemplateObject> namedArgs, Fragment body) {
        this.directive = directive;
        this.nameSpace = nameSpace;
        this.namedArgs = namedArgs;
        this.body = body;
    }

    @Override
    public void process(ProcessContext context) {
        context.getDirective(nameSpace, directive).execute(context, namedArgs, body);
    }

    @Override
    public int getSize() {
        return body.getSize() + 1;
    }

    @Override
    public <R> R accept(TemplateVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
