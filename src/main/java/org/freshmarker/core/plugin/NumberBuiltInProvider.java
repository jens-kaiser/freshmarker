package org.freshmarker.core.plugin;

import ftl.Token.TokenType;
import org.freshmarker.api.BuiltIn;
import org.freshmarker.api.extension.BuiltInProvider;
import org.freshmarker.api.extension.Register;
import org.freshmarker.api.extension.support.SingleTypeBuiltInRegister;
import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.ProcessException;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.model.primitive.TemplateNumber;
import org.freshmarker.core.model.primitive.TemplateNumber.Type;
import org.freshmarker.core.model.primitive.TemplateString;
import org.freshmarker.core.utils.RomanNumbers;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Formatter;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;

public final class NumberBuiltInProvider implements BuiltInProvider {

    private Number castBigInteger(Number number) {
        if (number instanceof BigDecimal bigDecimal) {
            return bigDecimal.toBigInteger();
        }
        return new BigInteger(String.valueOf(number));
    }

    private Number castBigDecimal(Number number) {
        return new BigDecimal(String.valueOf(number));
    }

    private static TemplateNumber getNumber(TemplateObject object) {
        return (TemplateNumber) object;
    }

    private static TemplateNumber getNumberParameter(List<TemplateObject> list) {
        BuiltInHelper.checkParametersLength(list, 1);
        if (list.getFirst() instanceof TemplateNumber number) {
            return number;
        }
        throw new ProcessException("expected TemplateNumber but found " + list.getFirst().getClass().getSimpleName());
    }

    private TemplateObject human(TemplateNumber value, ProcessContext context) {
        if (value.getType().isFloatingPoint()) {
            return value;
        }
        int number = value.getValue().intValue();
        if (number > 0 && number < 10) {
            return new TemplateString(ResourceBundle.getBundle("freshmarker", context.getLocale()).getString("number." + value));
        }
        return value;
    }

    private static TemplateString format(TemplateObject value, List<TemplateObject> parameters, ProcessContext context) {
        BuiltInHelper.checkParametersLength(parameters, 1);
        TemplateString format = parameters.getFirst().evaluate(context, TemplateString.class);
        try (Formatter formatter = new Formatter(context.getLocale())) {
            return new TemplateString(formatter.format(format.getValue(), getNumber(value).getValue()).toString());
        }
    }

    private static TemplateNumber cast(TemplateObject value, TemplateNumber.Type type, UnaryOperator<Number> converter) {
        TemplateNumber number = getNumber(value);
        return number.getType() == type ? number : TemplateNumber.of(converter.apply(number.getValue()), type);
    }

    private TemplateObject clamp(TemplateNumber value, ProcessContext context, List<TemplateObject> parameter) {
        BuiltInHelper.checkParametersLength(parameter, 2);
        TemplateNumber min = parameter.getFirst().evaluate(context, TemplateNumber.class);
        TemplateNumber max = parameter.get(1).evaluate(context, TemplateNumber.class);
        if (min.relation(TokenType.GT, max, context)) {
            throw new ProcessException(min + " > " + max);
        }
        TemplateNumber result = value.max(min).min(max);
        return TemplateNumber.of(result.getValue(), result.getType());
    }

    @Override
    public Register<Class<? extends TemplateObject>, String, BuiltIn> provideBuiltInRegister() {
        SingleTypeBuiltInRegister builtInRegister = new SingleTypeBuiltInRegister(TemplateNumber.class);
        builtInRegister.add("c", BuiltIn.string());
        builtInRegister.add("abs", (x, y, e) -> getNumber(x).abs());
        builtInRegister.add("sign", (x, y, e) -> getNumber(x).sign());
        builtInRegister.add("format", NumberBuiltInProvider::format);
        builtInRegister.add("int", (x, y, e) -> cast(x, Type.INTEGER, Number::intValue));
        builtInRegister.add("long", (x, y, e) -> cast(x, Type.LONG, Number::longValue));
        builtInRegister.add("short", (x, y, e) -> cast(x, Type.SHORT, Number::shortValue));
        builtInRegister.add("byte", (x, y, e) -> cast(x, Type.BYTE, Number::byteValue));
        builtInRegister.add("double", (x, y, e) -> cast(x, Type.DOUBLE, Number::doubleValue));
        builtInRegister.add("float", (x, y, e) -> cast(x, Type.FLOAT, Number::floatValue));
        builtInRegister.add("big_integer", (x, y, e) -> cast(x, Type.BIG_INTEGER, this::castBigInteger));
        builtInRegister.add("big_decimal", (x, y, e) -> cast(x, Type.BIG_DECIMAL, this::castBigDecimal));
        builtInRegister.add("roman", (x, y, e) -> RomanNumbers.roman(x));
        builtInRegister.add("utf_roman", (x, y, e) -> RomanNumbers.utfRoman(x));
        builtInRegister.add("clock_roman", (x, y, e) -> RomanNumbers.clockRoman(x));
        builtInRegister.add("h", (x, y, e) -> human(getNumber(x), e));
        builtInRegister.add("min", (x, y, e) -> getNumber(x).min(getNumberParameter(y)));
        builtInRegister.add("max", (x, y, e) -> getNumber(x).max(getNumberParameter(y)));
        builtInRegister.add("is_number", BuiltInHelper.alwaysTrue());
        builtInRegister.add("clamp", (x, y, e) -> clamp((TemplateNumber) x, e, y));
        return builtInRegister;
    }
}
