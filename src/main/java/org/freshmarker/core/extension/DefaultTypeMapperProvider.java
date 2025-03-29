package org.freshmarker.core.extension;

import org.freshmarker.api.TypeMapperProvider;
import org.freshmarker.core.TypeMapper;
import org.freshmarker.core.model.primitive.TemplateBoolean;
import org.freshmarker.core.model.primitive.TemplateLocale;
import org.freshmarker.core.model.primitive.TemplateNumber;
import org.freshmarker.core.model.primitive.TemplateNumber.Type;
import org.freshmarker.core.model.primitive.TemplateString;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class DefaultTypeMapperProvider implements TypeMapperProvider {
    @Override
    public Map<Class<?>, TypeMapper> providerTypeMapper() {
        return Map.ofEntries(
                Map.entry(String.class, o -> new TemplateString((String) o)),
                Map.entry(Boolean.class, o -> Boolean.TRUE.equals(o) ? TemplateBoolean.TRUE : TemplateBoolean.FALSE),
                Map.entry(AtomicLong.class, o -> new TemplateNumber((AtomicLong) o, Type.LONG)),
                Map.entry(AtomicInteger.class, o -> new TemplateNumber((AtomicInteger) o, Type.INTEGER)),
                Map.entry(Long.class, o -> new TemplateNumber((Long) o)),
                Map.entry(Integer.class, o -> TemplateNumber.of((Integer) o)),
                Map.entry(Short.class, o -> new TemplateNumber((Short) o)),
                Map.entry(Byte.class, o -> new TemplateNumber((Byte) o)),
                Map.entry(Double.class, o -> new TemplateNumber((Double) o)),
                Map.entry(Float.class, o -> new TemplateNumber((Float) o)),
                Map.entry(BigInteger.class, o -> new TemplateNumber((BigInteger) o)),
                Map.entry(BigDecimal.class, o -> new TemplateNumber((BigDecimal) o)),
                Map.entry(Locale.class, o -> new TemplateLocale((Locale) o))
        );
    }
}
