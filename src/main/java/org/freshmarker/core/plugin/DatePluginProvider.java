package org.freshmarker.core.plugin;

import org.freshmarker.api.TypeMapper;
import org.freshmarker.api.BuiltIn;
import org.freshmarker.api.extension.BuiltInProvider;
import org.freshmarker.api.Formatter;
import org.freshmarker.api.extension.FormatterProvider;
import org.freshmarker.api.extension.Register;
import org.freshmarker.api.extension.TypeMapperProvider;
import org.freshmarker.api.extension.support.BuiltInRegister;
import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.ProcessException;
import org.freshmarker.core.formatter.ClassicDateFormatter;
import org.freshmarker.core.formatter.ClassicDateTimeFormatter;
import org.freshmarker.core.formatter.ClassicTimeFormatter;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.model.date.TemplateClassicDate;
import org.freshmarker.core.model.date.TemplateClassicDateTime;
import org.freshmarker.core.model.date.TemplateClassicTime;
import org.freshmarker.core.model.primitive.TemplateBoolean;
import org.freshmarker.core.model.primitive.TemplateString;

import java.sql.Date;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

public final class DatePluginProvider implements BuiltInProvider, TypeMapperProvider, FormatterProvider {
    private static final EnumSet<ChronoUnit> SUPPORTED_TEMPORAL_UNITS = EnumSet.of(ChronoUnit.DAYS, ChronoUnit.MONTHS, ChronoUnit.YEARS);
    private static final String IS_TEMPORAL = "is_temporal";
    private static final String SUPPORTS = "supports";

    @Override
    public Register<Class<? extends TemplateObject>, String, BuiltIn> provideBuiltInRegister() {
        BuiltInRegister builtInRegister = new BuiltInRegister();
        builtInRegister.add(TemplateClassicDateTime.class, "date",
                (x, y, c) -> new TemplateClassicDate(new Date(((TemplateClassicDateTime) x).getValue().getTime())));
        builtInRegister.add(TemplateClassicDateTime.class, "time",
                (x, y, c) -> new TemplateClassicTime(new Time(((TemplateClassicDateTime) x).getValue().getTime())));
        builtInRegister.add(TemplateClassicDateTime.class, "c",
                (x, y, c) -> new TemplateString(new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss").format(((TemplateClassicDateTime) x).getValue())));
        builtInRegister.add(TemplateClassicDate.class, "date", BuiltIn.identity());
        builtInRegister.add(TemplateClassicDate.class, "c", (x, y, c) -> new TemplateString(String.valueOf(x)));
        builtInRegister.add(TemplateClassicTime.class, "time", BuiltIn.identity());
        builtInRegister.add(TemplateClassicTime.class, "c", (x, y, c) -> new TemplateString(String.valueOf(x)));
        builtInRegister.add(TemplateClassicDateTime.class, IS_TEMPORAL, BuiltInHelper.alwaysTrue());
        builtInRegister.add(TemplateClassicDate.class, IS_TEMPORAL, BuiltInHelper.alwaysTrue());
        builtInRegister.add(TemplateClassicTime.class, IS_TEMPORAL, BuiltInHelper.alwaysTrue());
        builtInRegister.add(TemplateClassicDateTime.class, SUPPORTS, this::supports);
        builtInRegister.add(TemplateClassicDate.class, SUPPORTS, this::supports);
        builtInRegister.add(TemplateClassicTime.class, SUPPORTS, (x, y, c) -> TemplateBoolean.FALSE);
        return builtInRegister;
    }

    private TemplateBoolean supports(TemplateObject value, List<TemplateObject> parameters, ProcessContext context) {
        BuiltInHelper.checkParametersLength(parameters, 1);
        ChronoUnit unit = ChronoUnit.valueOf(parameters.getFirst().evaluate(context, TemplateString.class).getValue());
        if (!SUPPORTED_TEMPORAL_UNITS.contains(unit))  {
            throw new ProcessException("unsupported temporal unit");
        }
        return TemplateBoolean.TRUE;
    }

    @Override
    public Map<Class<?>, TypeMapper> providerTypeMapper() {
        return Map.of(
            java.util.Date.class, o -> new TemplateClassicDateTime((java.util.Date) o),
            Date.class, o -> new TemplateClassicDate((Date) o),
            Time.class, o -> new TemplateClassicTime((Time) o));
    }

    @Override
    public Map<Class<? extends TemplateObject>, Formatter> providerFormatter() {
        return Map.of(
            TemplateClassicDateTime.class, new ClassicDateTimeFormatter("yyyy-MM-dd hh:mm:ss"),
            TemplateClassicDate.class, new ClassicDateFormatter("yyyy-MM-dd"),
            TemplateClassicTime.class, new ClassicTimeFormatter("hh:mm:ss"));
    }
}
