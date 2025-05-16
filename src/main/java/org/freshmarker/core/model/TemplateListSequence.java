package org.freshmarker.core.model;

import ftl.Token.TokenType;
import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.ProcessException;

import java.util.ArrayList;
import java.util.List;

public class TemplateListSequence implements TemplateSequence {

    private final List<Object> sequence;

    public TemplateListSequence(List<Object> sequence) {
        this.sequence = sequence;
    }

    public TemplateObject get(ProcessContext context, int index) {
        return context.mapObject(sequence.get(index));
    }

    @Override
    public int size(ProcessContext context) {
        return sequence.size();
    }

    private int checkLowerBound(int lowerBound) {
        if (lowerBound < 0) {
            throw new ProcessException("negative slicing values not allowed: " + lowerBound);
        }
        return lowerBound;
    }

    public TemplateListSequence slice(int min, int max) {
        if (min < max) {
            return new TemplateListSequence(sequence.subList(checkLowerBound(min), max + 1));
        }
        return new TemplateListSequence(sequence.subList(checkLowerBound(max), min + 1).reversed());
    }

    public TemplateListSequence slice(int min) {
        return new TemplateListSequence(sequence.subList(min, sequence.size()));
    }

    @Override
    public TemplateListSequence evaluateToObject(ProcessContext context) {
        return this;
    }

    public List<Object> getSequence() {
        return sequence;
    }

    @Override
    public TemplateObject operation(TokenType operator, TemplateObject operand, ProcessContext context) {
        TemplateObject value = operand.evaluateToObject(context);
        if (operator == TokenType.PLUS && value instanceof TemplateListSequence sequence2) {
            List<Object> newSequence = new ArrayList<>(sequence);
            newSequence.addAll(sequence2.sequence);
            return new TemplateListSequence(newSequence);
        }
        throw new ProcessException("unsupported operation: " + operator);
    }
}
