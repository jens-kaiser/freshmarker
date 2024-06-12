package org.freshmarker.core.ftl;

import ftl.Token.TokenType;
import ftl.Node;
import ftl.Token;
import ftl.ast.IDENTIFIER;
import ftl.ast.ParameterList;
import org.freshmarker.core.model.TemplateObject;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ParameterListBuilder implements
        ExpressionVisitor<List<ParameterHolder>, List<ParameterHolder>> {

    public static final ParameterListBuilder INSTANCE = new ParameterListBuilder();

    private ParameterListBuilder() {
        super();
    }

    @Override
    public List<ParameterHolder> visit(Token expression, List<ParameterHolder> input) {
        input.add(new ParameterHolder(expression.toString(), null));
        return input;
    }

    @Override
    public List<ParameterHolder> visit(ParameterList expression, List<ParameterHolder> input) {
        int index = 0;
        List<Node> children = expression.children().stream().filter(p -> TokenType.COMMA != p.getType()).toList();
        int maxChildren = children.size();
        Set<String> names = new HashSet<>();
        while (index < maxChildren) {
            IDENTIFIER identifier = (IDENTIFIER) children.get(index);
            String name = identifier.toString();
            if (names.contains(name)) {
                throw new ParsingException("non unique parameter name", identifier);
            }
            names.add(name);
            index++;
            if (index >= maxChildren) {
                input.add(new ParameterHolder(name, null));
                break;
            }
            if (children.get(index).getType() == TokenType.EQUALS) {
                index++;
                TemplateObject defaultValue = children.get(index).accept(InterpolationBuilder.INSTANCE, null);
                input.add(new ParameterHolder(name, defaultValue));
                index++;
            } else if (children.get(index).getType() == TokenType.ELLIPSIS) {
                throw new ParsingException("ellipsis not supported", children.get(index));
            } else {
                input.add(new ParameterHolder(name, null));
            }
        }
        return input;
    }
}
