package org.freshmarker.core.plugin;

import org.freshmarker.api.BuiltInProvider;
import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.ProcessException;
import org.freshmarker.core.buildin.BuiltIn;
import org.freshmarker.core.buildin.BuiltInKey;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.model.primitive.TemplateNumber;
import org.freshmarker.core.model.primitive.TemplateNumber.Type;
import org.freshmarker.core.model.primitive.TemplateString;
import org.freshmarker.core.utils.RomanNumbers;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Formatter;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.UnaryOperator;

public final class NumberPluginProvider implements BuiltInProvider {

    private Number castBigInteger(Number number) {
        return switch (number) {
            case Byte b -> new BigInteger(String.valueOf(b));
            case Short s -> new BigInteger(String.valueOf(s));
            case Integer i -> new BigInteger(String.valueOf(i));
            case Long l -> new BigInteger(String.valueOf(l));
            case AtomicInteger i -> new BigInteger(String.valueOf(i));
            case AtomicLong l -> new BigInteger(String.valueOf(l));
            case BigDecimal bd -> bd.toBigInteger();
            default -> throw new ProcessException("cannot cast " + number.getClass().getSimpleName() + " to BigInteger");
        };
    }

    private Number castBigDecimal(Number number) {
        return switch (number) {
            case Byte b -> new BigDecimal(String.valueOf(b));
            case Short s -> new BigDecimal(String.valueOf(s));
            case Integer i -> new BigDecimal(String.valueOf(i));
            case Long l -> new  BigDecimal(String.valueOf(l));
            case AtomicInteger i -> new BigDecimal(String.valueOf(i));
            case AtomicLong l -> new  BigDecimal(String.valueOf(l));
            case Float f -> new BigDecimal(String.valueOf(f));
            case Double d -> new  BigDecimal(String.valueOf(d));
            case BigInteger bi -> new BigDecimal(bi.toString());
            default -> throw new ProcessException("cannot cast " + number.getClass().getSimpleName() + " to BigDecimal");
        };
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

    @Override
    public Map<BuiltInKey, BuiltIn> provideBuiltIns() {
        MapEntryBuilder<TemplateNumber> builder = new MapEntryBuilder<>(TemplateNumber.class);
        return Map.ofEntries(
                builder.entry("c", BuiltIn.string()),
                builder.entry("abs", (x, y, e) -> getNumber(x).abs()),
                builder.entry("sign", (x, y, e) -> getNumber(x).sign()),
                builder.entry("format", NumberPluginProvider::format),
                builder.entry("int", (x, y, e) -> cast(x, Type.INTEGER, Number::intValue)),
                builder.entry("long", (x, y, e) -> cast(x, Type.LONG, Number::longValue)),
                builder.entry("short", (x, y, e) -> cast(x, Type.SHORT, Number::shortValue)),
                builder.entry("byte", (x, y, e) -> cast(x, Type.BYTE, Number::byteValue)),
                builder.entry("double", (x, y, e) -> cast(x, Type.DOUBLE, Number::doubleValue)),
                builder.entry("float", (x, y, e) -> cast(x, Type.FLOAT, Number::floatValue)),
                builder.entry("big_integer", (x, y, e) -> cast(x, Type.BIG_INTEGER, this::castBigInteger)),
                builder.entry("big_decimal", (x, y, e) -> cast(x, Type.BIG_DECIMAL, this::castBigDecimal)),
                builder.entry("roman", (x, y, e) -> RomanNumbers.roman(x)),
                builder.entry("utf_roman", (x, y, e) -> RomanNumbers.utfRoman(x)),
                builder.entry("clock_roman", (x, y, e) -> RomanNumbers.clockRoman(x)),
                builder.entry("h", (x, y, e) -> human(getNumber(x), e)),
                builder.entry("min", (x, y, e) -> getNumber(x).min(getNumberParameter(y))),
                builder.entry("max", (x, y, e) -> getNumber(x).max(getNumberParameter(y)))
        );
    }
}
