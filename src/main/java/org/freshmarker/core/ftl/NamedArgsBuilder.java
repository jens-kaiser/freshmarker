package org.freshmarker.core.ftl;

import ftl.Token.TokenType;
import ftl.Token;
import ftl.ast.IDENTIFIER;
import ftl.ast.NamedArgsList;
import org.freshmarker.core.ftl.TemplateDictionary.VariableType;
import org.freshmarker.core.model.TemplateObject;

import java.util.Map;

public class NamedArgsBuilder implements FtlVisitor<Map<String, TemplateObject>, Void> {

    private final InterpolationBuilder interpolationBuilder;
    private final TemplateDictionary dictionary;

    public NamedArgsBuilder(InterpolationBuilder interpolationBuilder, TemplateDictionary dictionary) {
        this.interpolationBuilder = interpolationBuilder;
        this.dictionary = dictionary;
    }

    @Override
    public Void visit(Token ftl, Map<String, TemplateObject> input) {
        return null;
    }

    @Override
    public Void visit(NamedArgsList ftl, Map<String, TemplateObject> input) {
        int i = 0;
        while (i < ftl.size()) {
            if (ftl.get(i).getType() == TokenType.COMMA) {
                i++;
            }
            IDENTIFIER key = (IDENTIFIER) ftl.get(i);
            TemplateObject value = ftl.get(i + 2).accept(interpolationBuilder, null);
            dictionary.putVariable(key.toString(), VariableType.ARG);
            input.put(key.toString(), value);
            i += 3;
        }
        return null;
    }
}
