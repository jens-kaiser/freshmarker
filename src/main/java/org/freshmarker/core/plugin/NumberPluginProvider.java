package org.freshmarker.core.plugin;

import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.ProcessException;
import org.freshmarker.core.buildin.BuiltIn;
import org.freshmarker.core.buildin.BuiltInKey;
import org.freshmarker.core.buildin.BuiltInKeyBuilder;
import org.freshmarker.core.formatter.NumberFormatter;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.model.primitive.TemplateNumber;
import org.freshmarker.core.model.primitive.TemplateNumber.Type;
import org.freshmarker.core.model.primitive.TemplateString;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Formatter;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public final class NumberPluginProvider implements PluginProvider {

    private static final String[] ONES = new String[]{"", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX"};
    private static final String[] TENS = new String[]{"", "X", "XX", "XXX", "XL", "L", "LX", "LXX", "LXXX", "XC"};
    private static final String[] HUNDREDS = new String[]{"", "C", "CC", "CCC", "CD", "D", "DC", "DCC", "DCCC", "CM"};
    private static final String[] THOUSANDS = new String[]{"", "M", "MM", "MMM"};

    private static final String[] UTF_ONES = new String[]{"", "Ⅰ", "ⅠⅠ", "ⅠⅠⅠ", "ⅠⅤ", "Ⅴ", "ⅤⅠ", "ⅤⅠⅠ", "ⅤⅠⅠⅠ", "ⅠⅩ"};
    private static final String[] UTF_TENS = new String[]{"", "Ⅹ", "Ⅹ", "ⅩⅩⅩ", "ⅩⅬ", "Ⅼ", "ⅬⅩ", "ⅬⅩⅩ", "ⅬⅩⅩⅩ", "ⅩⅭ"};
    private static final String[] UTF_HUNDREDS = new String[]{"", "Ⅽ", "ⅭⅭ", "ⅭⅭⅭ", "ⅭⅮ", "Ⅾ", "ⅮⅭ", "ⅮⅭⅭ", "ⅮⅭⅭⅭ", "ⅭⅯ"};
    private static final String[] UTF_THOUSANDS = new String[]{"", "Ⅿ", "ⅯⅯ", "ⅯⅯⅯ"};

    private static final BuiltInKeyBuilder<TemplateNumber> BUILDER = new BuiltInKeyBuilder<>(TemplateNumber.class);

    @Override
    public void registerMapper(Map<Class<?>, Function<Object, TemplateObject>> mapper) {
        mapper.put(AtomicLong.class, o -> new TemplateNumber((AtomicLong) o, Type.LONG));
        mapper.put(AtomicInteger.class, o -> new TemplateNumber((AtomicInteger) o, Type.INTEGER));
        mapper.put(Long.class, o -> new TemplateNumber((Long) o));
        mapper.put(Integer.class, o -> TemplateNumber.of((Integer) o));
        mapper.put(Short.class, o -> new TemplateNumber((Short) o));
        mapper.put(Byte.class, o -> new TemplateNumber((Byte) o));
        mapper.put(Double.class, o -> new TemplateNumber((Double) o));
        mapper.put(Float.class, o -> new TemplateNumber((Float) o));
        mapper.put(BigInteger.class, o -> new TemplateNumber((BigInteger) o));
        mapper.put(BigDecimal.class, o -> new TemplateNumber((BigDecimal) o));
    }

    @Override
    public void registerBuildIn(Map<BuiltInKey, BuiltIn> builtIns) {
        builtIns.put(BUILDER.of("c"), (x, y, e) -> new TemplateString(String.valueOf(x)));
        builtIns.put(BUILDER.of("abs"), (x, y, e) -> getNumber(x).abs());
        builtIns.put(BUILDER.of("sign"), (x, y, e) -> getNumber(x).sign());
        builtIns.put(BUILDER.of("format"), NumberPluginProvider::format);
        builtIns.put(BUILDER.of("int"), (x, y, e) -> cast(x, Type.INTEGER, Number::intValue));
        builtIns.put(BUILDER.of("long"), (x, y, e) -> cast(x, Type.LONG, Number::longValue));
        builtIns.put(BUILDER.of("short"), (x, y, e) -> cast(x, Type.SHORT, Number::shortValue));
        builtIns.put(BUILDER.of("byte"), (x, y, e) -> cast(x, Type.BYTE, Number::byteValue));
        builtIns.put(BUILDER.of("double"), (x, y, e) -> cast(x, Type.DOUBLE, Number::doubleValue));
        builtIns.put(BUILDER.of("float"), (x, y, e) -> cast(x, Type.FLOAT, Number::floatValue));
        builtIns.put(BUILDER.of("big_integer"), (x, y, e) -> cast(x, Type.BIG_INTEGER, this::castBigInteger));
        builtIns.put(BUILDER.of("big_decimal"), (x, y, e) -> cast(x, Type.BIG_DECIMAL, this::castBigDecimal));
        builtIns.put(BUILDER.of("roman"), (x, y, e) -> roman(x));
        builtIns.put(BUILDER.of("utf_roman"), (x, y, e) -> utfRoman(x));
        builtIns.put(BUILDER.of("clock_roman"), (x, y, e) -> clockRoman(x));
        builtIns.put(BUILDER.of("h"), (x, y, e) -> human(getNumber(x), e));
        builtIns.put(BUILDER.of("min"), (x, y, e) -> getNumber(x).min(getNumberParameter(y)));
        builtIns.put(BUILDER.of("max"), (x, y, e) -> getNumber(x).max(getNumberParameter(y)));
    }

    @Override
    public void registerFormatter(Map<Class<? extends TemplateObject>, org.freshmarker.core.formatter.Formatter> formatter) {
        formatter.put(TemplateNumber.class, new NumberFormatter());
    }

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

    public static TemplateString roman(TemplateObject value) {
        return new TemplateString(toRoman(checkRomanNumber(getNumber(value))));
    }

    public static TemplateString utfRoman(TemplateObject value) {
        return new TemplateString(toUtfRoman(checkRomanNumber(getNumber(value))));
    }

    public static TemplateString clockRoman(TemplateObject value) {
        int numberValue = getNumber(value).asInt();
        if (numberValue < 1 || numberValue > 12) {
            throw new ProcessException("roman clock numerals only between 1 and 12");
        }
        return new TemplateString(String.valueOf((char) (numberValue + 0x215F)));
    }

    private static int checkRomanNumber(TemplateNumber value) {
        int number = value.asInt();
        if (number < 1 || number > 3999) {
            throw new ProcessException("roman numerals only between 1 and 3999: " + number);
        }
        return number;
    }

    private static String toRoman(int number) {
        return THOUSANDS[number / 1000] + HUNDREDS[(number % 1000) / 100] + TENS[(number % 100) / 10] + ONES[number % 10];
    }

    private static String toUtfRoman(int number) {
        return UTF_THOUSANDS[number / 1000] + UTF_HUNDREDS[(number % 1000) / 100] + UTF_TENS[(number % 100) / 10] + UTF_ONES[number % 10];
    }
}
