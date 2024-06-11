package org.freshmarker.core.ftl;

import ftl.FreshMarkerParser;
import ftl.ast.ParameterList;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class ParameterListBuilderTest {

    @Test
    void emptyParameterList() {
        FreshMarkerParser parser = new FreshMarkerParser("<#macro test()></#macro>");
        parser.setInputSource("parameter-list");
        parser.Root();
        assertNull(parser.rootNode().firstDescendantOfType(ParameterList.class));
    }

    @Test
    void parameterList() {
        FreshMarkerParser parser = new FreshMarkerParser("<#macro test(parameter1, parameter2)></#macro>");
        parser.setInputSource("parameter-list");
        parser.Root();
        ParameterList parameterList = parser.rootNode().firstDescendantOfType(ParameterList.class);
        assertNotNull(parameterList);
        ArrayList<ParameterHolder> parameterHolders = new ArrayList<>();
        parameterList.accept(ParameterListBuilder.INSTANCE, parameterHolders);
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
        parameterList.accept(ParameterListBuilder.INSTANCE, parameterHolders);
        assertEquals(2, parameterHolders.size());
        ParameterHolder first = parameterHolders.getFirst();
        assertAll(
                () -> assertNotNull(first),
                () -> assertEquals("parameter1", first.name()),
                () -> assertEquals(42, first.defaultValue().asNumber().orElseThrow().getValue().getNumber())
        );
        ParameterHolder last = parameterHolders.getLast();
        assertAll(
                () -> assertNotNull(last),
                () -> assertEquals("parameter2", last.name()),
                () -> assertEquals("test", last.defaultValue().asString().orElseThrow().getValue())
        );
    }
}