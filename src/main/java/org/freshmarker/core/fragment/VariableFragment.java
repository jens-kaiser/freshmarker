package org.freshmarker.core.fragment;

import ftl.Node;
import org.freshmarker.core.Environment;
import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.ProcessException;
import org.freshmarker.core.model.TemplateObject;

public class VariableFragment implements Fragment {

    private final String name;
    private final TemplateObject expression;

    private final boolean exists;

    private final Node node;

    public VariableFragment(String name, TemplateObject expression, boolean exists, Node node) {
        this.name = name;
        this.expression = expression;
        this.exists = exists;
        this.node = node;
    }

    @Override
    public void process(ProcessContext context) {
        Environment environment = context.getEnvironment();
        boolean checked = environment.checkVariable(name);
        if (exists) {
            processSetVariable(context, checked, environment);
        } else {
            processCreateVariable(context, checked, environment);
        }
    }

    private void processCreateVariable(ProcessContext context, boolean checked, Environment environment) {
        if (checked) {
            throw new ProcessException("variable " + name + " must not exist", node);
        }
        environment.createVariable(name, expression.evaluateToObject(context));
    }

    private void processSetVariable(ProcessContext context, boolean checked, Environment environment) {
        if (!checked) {
            throw new ProcessException("variable " + name + " must exists", node);
        }
        environment.setVariable(name, expression.evaluateToObject(context));
    }
}
