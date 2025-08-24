package org.freshmarker.core.ftl;

import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public final class TemplateDictionary {
    public enum VariableType {
        MODEL,
        KEY,
        VALUE,
        LOOPER,
        VAR,
        ARG
    }

    private final Deque<Map<String, VariableType>> stack = new LinkedList<>();

    public TemplateDictionary() {
        stack.add(new HashMap<>());
    }

    public VariableType getVariable(String name) {
        for (Map<String, VariableType> variables : stack) {
            VariableType type = variables.get(name);
            if (type != null) {
                return type;
            }
        }
        VariableType type = VariableType.MODEL;
        stack.getFirst().put(name, type);
        return type;
    }

    public void push() {
        stack.push(new HashMap<>());
    }

    public void poll() {
        stack.poll();
    }

    public void putVariable(String name, VariableType type) {
        stack.getFirst().put(name, type);
    }
}
