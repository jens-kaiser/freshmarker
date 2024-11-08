package org.freshmarker.core.formatter;

import org.freshmarker.core.ProcessException;
import org.freshmarker.core.formatter.Formatter;
import org.freshmarker.core.formatter.LocaleLocal;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.model.primitive.TemplateNumber;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class NumberFormatter implements Formatter {

    private final LocaleLocal<NumberFormat> numberFormat;

    public NumberFormatter() {
        this.numberFormat = LocaleLocal.withInitial(NumberFormat::getNumberInstance);
    }

    @Override
    public String format(TemplateObject object, Locale locale) {
        TemplateNumber number = (TemplateNumber) object;
        final NumberFormat format = numberFormat.get(locale);
        return switch (number.getValue()) {
            case Short n -> format.format(n.doubleValue());
            case Byte n -> format.format(n.doubleValue());
            case Integer n -> format.format(n.doubleValue());
            case Long n -> format.format(n.doubleValue());
            case Float n -> format.format(n.doubleValue());
            case Double n -> format.format(n.doubleValue());
            case AtomicInteger n -> format.format(n.doubleValue());
            case AtomicLong n -> format.format(n.doubleValue());
            case BigInteger n -> format.format(n);
            case BigDecimal n -> format.format(n);
            default -> throw new ProcessException("Unexpected number type: " + number.getValue());
        };
    }
}