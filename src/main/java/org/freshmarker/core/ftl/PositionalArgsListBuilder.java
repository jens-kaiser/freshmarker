package org.freshmarker.core.ftl;

import ftl.ast.PositionalArgsList;
import org.freshmarker.core.model.TemplateObject;

import java.util.List;

public class PositionalArgsListBuilder implements ExpressionVisitor<List<TemplateObject>, List<TemplateObject>> {
    @Override
    public List<TemplateObject> visit(PositionalArgsList expression, List<TemplateObject> input) {
        for (int i = 0; i < expression.size(); i += 2) {
            input.add(expression.get(i).accept(InterpolationBuilder.INSTANCE, null));
        }
        return input;
    }
}
