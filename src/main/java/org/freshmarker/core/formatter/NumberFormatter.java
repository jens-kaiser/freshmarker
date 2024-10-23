package org.freshmarker.core.formatter;

import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.model.primitive.TemplateNumber;

import java.text.NumberFormat;
import java.util.Locale;

public class NumberFormatter implements Formatter {

    private final LocaleLocal<NumberFormat> numberFormat;

    public NumberFormatter() {
        this.numberFormat = LocaleLocal.withInitial(NumberFormat::getNumberInstance);
    }

    @Override
    public String format(TemplateObject object, Locale locale) {
        TemplateNumber number = (TemplateNumber) object;
        return numberFormat.get(locale).format(number.getValue());
    }
}
