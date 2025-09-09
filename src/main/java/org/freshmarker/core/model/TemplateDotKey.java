package org.freshmarker.core.model;

import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.ReduceContext;
import org.freshmarker.core.WrongTypeException;

public class TemplateDotKey implements TemplateExpression {

    private final TemplateObject map;
    private final String dotKey;

    public TemplateDotKey(TemplateObject map, String dotKey) {
        this.map = map;
        this.dotKey = dotKey;
    }

    @Override
    public TemplateObject evaluateToObject(ProcessContext context) {
        TemplateObject templateObject = map.evaluateToObject(context);
        return switch (templateObject) {
            case TemplateNull templateNull -> templateNull;
            case DotHashAddressable addressable -> addressable.get(context, dotKey);
            case null, default -> throw new WrongTypeException("wrong map type: " + dotKey + " " + templateObject);
        };
    }

    @Override
    public <R> R accept(TemplateObjectVisitor<R> visitor) {
        return visitor.visit(this, map, dotKey);
    }

    @Override
    public TemplateObject reduce(ReduceContext context) {
        TemplateObject templateObject = map.evaluateToObject(context);
        if (templateObject instanceof DotHashAddressable addressable) {
            TemplateObject result = addressable.get(context, dotKey);
            if (result != TemplateNull.NULL) {
                return result;
            }
        }
        return this;
    }
}
