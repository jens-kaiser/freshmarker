package org.freshmarker.core.model;

import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.ProcessException;
import org.freshmarker.core.model.primitive.TemplateCharacter;
import org.freshmarker.core.model.primitive.TemplateNumber;
import org.freshmarker.core.model.primitive.TemplateString;

public class TemplateDynamicKey implements TemplateExpression {

    private final TemplateObject sequenceOrMap;
    private final TemplateObject dynamicKey;

    public TemplateDynamicKey(TemplateObject sequenceOrMap, TemplateObject dynamicKey) {
        this.sequenceOrMap = sequenceOrMap;
        this.dynamicKey = dynamicKey;
    }

    @Override
    public TemplateObject evaluateToObject(ProcessContext context) {
        TemplateObject templateObject = sequenceOrMap.evaluateToObject(context);
        if (templateObject == TemplateNull.NULL) {
            return TemplateNull.NULL;
        }
        TemplateObject key = dynamicKey.evaluateToObject(context);
        try {
            return switch (key) {
                case TemplateNumber number -> handleIndex(context, templateObject, number);
                case TemplateRange range -> handleRange(context, templateObject, range);
                case TemplateString name -> handleHash(context, templateObject, name);
                default -> throw new ProcessException("unsupported type: " + key.getModelType());
            };
        } catch (ProcessException e) {
            throw e;
        } catch (RuntimeException e) {
            throw new ProcessException(e.getMessage());
        }
    }

    private TemplateObject handleHash(ProcessContext context, TemplateObject templateObject, TemplateString name) {
        if (templateObject instanceof DotHashAddressable dotHashAddressable) {
            return dotHashAddressable.get(context, name.getValue());
        }
        throw new ProcessException("unsupported type: " + templateObject.getModelType());
    }

    private TemplateObject handleRange(ProcessContext context, TemplateObject templateObject, TemplateRange range) {
        return new TemplateSlice(templateObject, range).evaluateToObject(context);
    }

    private TemplateObject handleIndex(ProcessContext context, TemplateObject templateObject, TemplateNumber index) {
        int beginIndex = index.asInt();
        return switch (templateObject) {
            case TemplateRange range -> {
                TemplateNumber lower = range.getLower().evaluate(context, TemplateNumber.class);
                if (range.isRightUnlimited()) {
                    yield lower.add(index);
                }
                TemplateNumber upper = range.getUpper(context).evaluate(context, TemplateNumber.class);
                if (Math.abs(lower.asInt() - upper.asInt()) <= index.asInt()) {
                    throw new ProcessException("index out of range: " + index);
                }
                yield lower.add(lower.asInt() < upper.asInt() ? index : index.negate());
            }
            case TemplateString templateString -> getStringIndexResult(templateString, beginIndex);
            case TemplateSequence<?> sequence -> context.mapObject(sequence.sequence().get(beginIndex));
            default -> throw new ProcessException("unsupported type: " + templateObject.getModelType());
        };
    }

    protected TemplateObject getStringIndexResult(TemplateString templateString, int beginIndex) {
        return new TemplateCharacter(templateString.getValue().charAt(beginIndex));
    }

    @Override
    public <R> R accept(TemplateObjectVisitor<R> visitor) {
        return visitor.visit(this, sequenceOrMap, dynamicKey);
    }
}
