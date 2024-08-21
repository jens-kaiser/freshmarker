package org.freshmarker.core.plugin;

import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.buildin.BuiltIn;
import org.freshmarker.core.buildin.BuiltInKey;
import org.freshmarker.core.buildin.BuiltInMethod;
import org.freshmarker.core.model.primitive.TemplateNumber;
import org.freshmarker.core.model.primitive.TemplateString;

import java.util.Formatter;
import java.util.Map;

public class NumberPluginProvider implements PluginProvider {

    private static final String[] ONES = new String[]{"", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX"};
    private static final String[] TENS = new String[]{"", "X", "XX", "XXX", "XL", "L", "LX", "LXX", "LXXX", "XC"};
    private static final String[] HUNDREDS = new String[]{"", "C", "CC", "CCC", "CD", "D", "DC", "DCC", "DCCC", "CM"};
    private static final String[] THOUSANDS = new String[]{"", "M", "MM", "MMM"};

    private static final String[] UTF_ONES = new String[]{"", "Ⅰ", "ⅠⅠ", "ⅠⅠⅠ", "ⅠⅤ", "Ⅴ", "ⅤⅠ", "ⅤⅠⅠ", "ⅤⅠⅠⅠ", "ⅠⅩ"};
    private static final String[] UTF_TENS = new String[]{"", "Ⅹ", "Ⅹ", "ⅩⅩⅩ", "ⅩⅬ", "Ⅼ", "ⅬⅩ", "ⅬⅩⅩ", "ⅬⅩⅩⅩ", "ⅩⅭ"};
    private static final String[] UTF_HUNDREDS = new String[]{"", "Ⅽ", "ⅭⅭ", "ⅭⅭⅭ", "ⅭⅮ", "Ⅾ", "ⅮⅭ", "ⅮⅭⅭ", "ⅮⅭⅭⅭ", "ⅭⅯ"};
    private static final String[] UTF_THOUSANDS = new String[]{"", "Ⅿ", "ⅯⅯ", "ⅯⅯⅯ"};

    @Override
    public void registerBuildIn(Map<BuiltInKey, BuiltIn> builtIns) {
        new MethodBuiltInHelper().registerBuiltIns(this, builtIns);
    }

    @BuiltInMethod("c")
    public static TemplateString computerBuiltIn(TemplateNumber value) {
        return new TemplateString(String.valueOf(value));
    }

    @BuiltInMethod
    public static TemplateNumber abs(TemplateNumber value) {
        return value.abs();
    }

    @BuiltInMethod
    public static TemplateNumber sign(TemplateNumber value) {
        return value.sign();
    }

    @BuiltInMethod
    public static TemplateString format(TemplateNumber value, ProcessContext context, TemplateString format) {
        try (Formatter formatter = new Formatter(context.getEnvironment().getLocale())) {
            return new TemplateString(formatter.format(format.getValue(), value.getValue().getNumber()).toString());
        }
    }

    @BuiltInMethod("int")
    public static TemplateNumber castInt(TemplateNumber value) {
        return value.getValue().getType() == TemplateNumber.Type.INTEGER ? value : new TemplateNumber(value.getValue().getNumber().intValue());
    }

    @BuiltInMethod("long")
    public static TemplateNumber castLong(TemplateNumber value) {
        return value.getValue().getType() == TemplateNumber.Type.LONG ? value : new TemplateNumber(value.getValue().getNumber().longValue());
    }

    @BuiltInMethod("short")
    public static TemplateNumber castShort(TemplateNumber value) {
        return value.getValue().getType() == TemplateNumber.Type.SHORT ? value : new TemplateNumber(value.getValue().getNumber().shortValue());
    }

    @BuiltInMethod("byte")
    public static TemplateNumber castByte(TemplateNumber value) {
        return value.getValue().getType() == TemplateNumber.Type.BYTE ? value : new TemplateNumber(value.getValue().getNumber().byteValue());
    }

    @BuiltInMethod("double")
    public static TemplateNumber castDouble(TemplateNumber value) {
        return value.getValue().getType() == TemplateNumber.Type.DOUBLE ? value : new TemplateNumber(value.getValue().getNumber().doubleValue());
    }

    @BuiltInMethod("float")
    public static TemplateNumber castFloat(TemplateNumber value) {
        return value.getValue().getType() == TemplateNumber.Type.FLOAT ? value : new TemplateNumber(value.getValue().getNumber().floatValue());
    }

    @BuiltInMethod
    public static TemplateString roman(TemplateNumber value) {
        return new TemplateString(toRoman(checkRomanNumber(value)));
    }

    @BuiltInMethod("utf_roman")
    public static TemplateString utfRoman(TemplateNumber value) {
        return new TemplateString(toUtfRoman(checkRomanNumber(value)));
    }

    @BuiltInMethod("clock_roman")
    public static TemplateString clockRoman(TemplateNumber value) {
        int number = value.asInt();
        if (number < 1 || number > 12) {
            throw new IllegalArgumentException("roman clock numerals only between 1 and 12");
        }
        return new TemplateString(String.valueOf((char) (number + 0x215F)));
    }

    private static int checkRomanNumber(TemplateNumber value) {
        int number = value.asInt();
        if (number < 1 || number > 3999) {
            throw new IllegalArgumentException("roman numerals only between 1 and 3999");
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
