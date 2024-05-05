package org.freshmarker.core.ftl;

import ftl.Token.TokenType;
import ftl.Token;
import ftl.ast.IDENTIFIER;
import ftl.ast.NamedArgsList;
import org.freshmarker.core.model.TemplateObject;

import java.util.Map;

public class NamedArgsBuilder implements FtlVisitor<Map<String, TemplateObject>, Void> {

    public static final NamedArgsBuilder INSTANCE = new NamedArgsBuilder();

    private NamedArgsBuilder() {
        super();
    }

    @Override
    public Void visit(Token ftl, Map<String, TemplateObject> input) {
        return null;
    }

    @Override
    public Void visit(NamedArgsList ftl, Map<String, TemplateObject> input) {
        int i = 0;
        while (i < ftl.getChildCount()) {
            if (ftl.getChild(i).getType() == TokenType.COMMA) {
                i++;
            }
            IDENTIFIER key = (IDENTIFIER) ftl.getChild(i);
            TemplateObject value = ftl.getChild(i + 2).accept(InterpolationBuilder.INSTANCE, null);
            input.put(key.toString(), value);
            i += 3;
        }
        return null;
    }
}
