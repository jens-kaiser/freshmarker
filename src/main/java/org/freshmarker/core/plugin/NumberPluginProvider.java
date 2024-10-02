package org.freshmarker.core.plugin;

import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.ProcessException;
import org.freshmarker.core.buildin.BuiltIn;
import org.freshmarker.core.buildin.BuiltInKey;
import org.freshmarker.core.buildin.BuiltInKeyBuilder;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.model.primitive.TemplateNumber;
import org.freshmarker.core.model.primitive.TemplateNumber.Type;
import org.freshmarker.core.model.primitive.TemplateString;

import java.util.Formatter;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;

public class NumberPluginProvider implements PluginProvider {

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
    public void registerBuildIn(Map<BuiltInKey, BuiltIn> builtIns) {
        builtIns.put(BUILDER.of("c"), (x, y, e) -> new TemplateString(String.valueOf(x)));
        builtIns.put(BUILDER.of("abs"), (x, y, e) -> getNumberParameter(x).abs());
        builtIns.put(BUILDER.of("sign"), (x, y, e) -> getNumberParameter(x).sign());
        builtIns.put(BUILDER.of("format"), NumberPluginProvider::format);
        builtIns.put(BUILDER.of("int"), (x, y, e) -> cast(x, Type.INTEGER, Number::intValue));
        builtIns.put(BUILDER.of("long"), (x, y, e) -> cast(x, Type.LONG, Number::longValue));
        builtIns.put(BUILDER.of("short"), (x, y, e) -> cast(x, Type.SHORT, Number::shortValue));
        builtIns.put(BUILDER.of("byte"), (x, y, e) -> cast(x, Type.BYTE, Number::byteValue));
        builtIns.put(BUILDER.of("double"), (x, y, e) -> cast(x, Type.DOUBLE, Number::doubleValue));
        builtIns.put(BUILDER.of("float"), (x, y, e) -> cast(x, Type.FLOAT, Number::floatValue));
        builtIns.put(BUILDER.of("roman"), (x, y, e) -> roman(x));
        builtIns.put(BUILDER.of("utf_roman"), (x, y, e) -> utfRoman(x));
        builtIns.put(BUILDER.of("clock_roman"), (x, y, e) -> clockRoman(x));
        builtIns.put(BUILDER.of("h"), (x, y, e) -> human(getNumberParameter(x), e));
        builtIns.put(BUILDER.of("min"), (x, y, e) -> getNumberParameter(x).min(getNumberParameter(y.getFirst())));
        builtIns.put(BUILDER.of("max"), (x, y, e) -> getNumberParameter(x).max(getNumberParameter(y.getFirst())));
    }

    private static TemplateNumber getNumberParameter(TemplateObject object) {
        if (object instanceof TemplateNumber number) {
            return number;
        }
        throw new ProcessException("expected TemplateNumber but found " + object.getClass().getSimpleName());
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
            return new TemplateString(formatter.format(format.getValue(), getNumberParameter(value).getValue()).toString());
        }
    }

    private static TemplateNumber cast(TemplateObject value, TemplateNumber.Type type, UnaryOperator<Number> converter) {
        TemplateNumber number = getNumberParameter(value);
        return number.getType() == type ? number : TemplateNumber.of(converter.apply(number.getValue()), type);
    }

    public static TemplateString roman(TemplateObject value) {
        return new TemplateString(toRoman(checkRomanNumber(getNumberParameter(value))));
    }

    public static TemplateString utfRoman(TemplateObject value) {
        return new TemplateString(toUtfRoman(checkRomanNumber(getNumberParameter(value))));
    }

    public static TemplateString clockRoman(TemplateObject value) {
        int numberValue = getNumberParameter(value).asInt();
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
