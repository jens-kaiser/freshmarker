package org.freshmarker.core.ftl;

import ftl.FreshMarkerParser;
import ftl.ast.ParameterList;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
        FreshMarkerParser parser = new FreshMarkerParser("<#macro test(parameter1, parameter2, parameter)></#macro>");
        parser.setInputSource("parameter-list");
        parser.Root();
        ParameterList parameterList = parser.rootNode().firstDescendantOfType(ParameterList.class);
        assertNotNull(parameterList);
        ArrayList<ParameterHolder> parameterHolders = new ArrayList<>();
        parameterList.accept(ParameterListBuilder.INSTANCE, parameterHolders);
        assertEquals(3, parameterHolders.size());
        assertEquals("parameter1", parameterHolders.getFirst().name());
        assertNull(parameterHolders.getFirst().defaultValue());
        assertEquals("parameter2", parameterHolders.get(1).name());
    }
}