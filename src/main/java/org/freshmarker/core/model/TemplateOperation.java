package org.freshmarker.core.model;

import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.ProcessException;

public interface TemplateOperation {
    default TemplateObject add(TemplateObject operand, ProcessContext context) {
        throw new ProcessException("unsupported operation: *");
    }

    default TemplateObject subtract(TemplateObject operand, ProcessContext context) {
        throw new ProcessException("unsupported operation: *");
    }

    default TemplateObject multiply(TemplateObject operand, ProcessContext context) {
        throw new ProcessException("unsupported operation: *");
    }

    default TemplateObject divide(TemplateObject operand, ProcessContext context) {
        throw new ProcessException("unsupported operation: /");
    }

    default TemplateObject modulo(TemplateObject operand, ProcessContext context) {
        throw new ProcessException("unsupported operation: %");
    }

    default TemplateObject negate() {
        throw new ProcessException("unsupported operation: negate");
    }
}
