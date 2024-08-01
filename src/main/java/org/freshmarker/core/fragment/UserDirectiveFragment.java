package org.freshmarker.core.fragment;

import org.freshmarker.core.Environment;
import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.environment.VariableEnvironment;
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
        Environment environment = context.getEnvironment();
        try {
            context.setEnvironment(new VariableEnvironment(environment));
            context.getEnvironment().getDirective(nameSpace, directive).execute(context, namedArgs, body);
        } finally {
            context.setEnvironment(environment);
        }
    }

    @Override
    public int getSize() {
        return body.getSize() + 1;
    }
}
