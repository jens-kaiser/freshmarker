package org.freshmarker.core.model.builtin;

import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.model.TemplateExpression;
import org.freshmarker.core.model.TemplateObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogBuiltIn implements TemplateExpression {
    private static final Logger logger = LoggerFactory.getLogger("builtin.logging");

    private final TemplateObject expression;
    private final String node;

    public LogBuiltIn(TemplateObject expression, String node) {
        this.expression = expression;
        this.node = node;
    }

    @Override
    public TemplateObject evaluateToObject(ProcessContext context) {
        TemplateObject result = expression.evaluateToObject(context);
        logger.debug("{} => {}", node, result);
        return result;
    }
}
