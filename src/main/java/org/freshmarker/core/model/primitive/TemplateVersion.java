package org.freshmarker.core.model.primitive;
import ftl.Token;
import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.ProcessException;
import org.freshmarker.core.model.DotHashAddressable;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.model.version.Version;

public class TemplateVersion extends TemplatePrimitive<Version> implements DotHashAddressable {

    public TemplateVersion(String value) {
        super(Version.byString(value));
    }

    public TemplateBoolean isBefore(TemplateVersion value) {
        return TemplateBoolean.from(getValue().compareTo(value.getValue()) < 0);
    }

    public TemplateBoolean isEqual(TemplateVersion value) {
        return TemplateBoolean.from(getValue().equals(value.getValue()));
    }

    public TemplateBoolean isAfter(TemplateVersion value) {
        return TemplateBoolean.from(getValue().compareTo(value.getValue()) > 0);
    }

    @Override
    public String toString() {
        return getValue().toString();
    }

    @Override
    public TemplatePrimitive<?> relational(Token.TokenType operator, TemplatePrimitive<?>  operand, ProcessContext context) {
        TemplateVersion rightValue = (TemplateVersion)operand;
        return compareValues(operator, getValue().compareTo(rightValue.getValue()));
    }

    public TemplateObject get(ProcessContext context, String name) {
       return switch (name) {
           case "major" -> TemplateNumber.of(getValue().major());
           case "minor" -> TemplateNumber.of(getValue().minor());
           case "patch" -> TemplateNumber.of(getValue().patch());
           default -> throw new ProcessException("unknown attribute: " + name);
       };
    }
}
