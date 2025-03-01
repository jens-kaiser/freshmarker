package org.freshmarker.core.ftl;

import ftl.FreshMarkerParser;
import ftl.ast.ParameterList;
import org.freshmarker.core.model.primitive.TemplateNumber;
import org.freshmarker.core.model.primitive.TemplateString;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ParameterListBuilderTest {

    private static final ParameterListBuilder PARAMETER_LIST_BUILDER = new ParameterListBuilder(new InterpolationBuilder(null));

    @Test
    void emptyParameterList() {
        FreshMarkerParser parser = new FreshMarkerParser("<#macro test()></#macro>");
        parser.setInputSource("parameter-list");
        parser.Root();
        assertNull(parser.rootNode().firstDescendantOfType(ParameterList.class));
    }

    @Test
    void parameterListWithEllipsis() {
        FreshMarkerParser parser = new FreshMarkerParser("<#macro test(parameter...)></#macro>");
        parser.setInputSource("parameter-list");
        parser.Root();
        ParameterList parameterList = parser.rootNode().firstDescendantOfType(ParameterList.class);
        assertNotNull(parameterList);
        List<ParameterHolder> parameterHolders = new ArrayList<>();
        assertThrows(ParsingException.class, () -> parameterList.accept(PARAMETER_LIST_BUILDER, parameterHolders));
    }

    @Test
    void parameterList() {
        FreshMarkerParser parser = new FreshMarkerParser("<#macro test(parameter1, parameter2)></#macro>");
        parser.setInputSource("parameter-list");
        parser.Root();
        ParameterList parameterList = parser.rootNode().firstDescendantOfType(ParameterList.class);
        assertNotNull(parameterList);
        List<ParameterHolder> parameterHolders = new ArrayList<>();
        parameterList.accept(PARAMETER_LIST_BUILDER, parameterHolders);
        assertEquals(2, parameterHolders.size());
        ParameterHolder first = parameterHolders.getFirst();
        assertAll(
                () -> assertNotNull(first),
                () -> assertEquals("parameter1", first.name()),
                () -> assertNull(first.defaultValue())
        );
        ParameterHolder last = parameterHolders.getLast();
        assertAll(
                () -> assertNotNull(last),
                () -> assertEquals("parameter2", last.name()),
                () -> assertNull(last.defaultValue())
        );
    }

    @Test
    void parameterListWithDefaultValues() {
        FreshMarkerParser parser = new FreshMarkerParser("<#macro test(parameter1 = 42, parameter2 = 'test')></#macro>");
        parser.setInputSource("parameter-list");
        parser.Root();
        ParameterList parameterList = parser.rootNode().firstDescendantOfType(ParameterList.class);
        assertNotNull(parameterList);
        ArrayList<ParameterHolder> parameterHolders = new ArrayList<>();
        parameterList.accept(PARAMETER_LIST_BUILDER, parameterHolders);
        assertEquals(2, parameterHolders.size());
        ParameterHolder first = parameterHolders.getFirst();
        assertAll(
                () -> assertNotNull(first),
                () -> assertEquals("parameter1", first.name()),
                () -> assertEquals(42, first.defaultValue() instanceof TemplateNumber number ? number.getValue() : null)
        );
        ParameterHolder last = parameterHolders.getLast();
        assertAll(
                () -> assertNotNull(last),
                () -> assertEquals("parameter2", last.name()),
                () -> assertEquals("test", last.defaultValue() instanceof TemplateString string ? string.getValue() : null)
        );
    }
}