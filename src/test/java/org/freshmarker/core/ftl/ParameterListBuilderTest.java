package org.freshmarker.core.ftl;

import ftl.FreshMarkerParser;
import ftl.Node;
import ftl.ast.AndExpression;
import ftl.ast.BaseExpression;
import ftl.ast.BooleanLiteral;
import ftl.ast.BuiltIn;
import ftl.ast.BuiltinVariable;
import ftl.ast.DefaultToExpression;
import ftl.ast.DotKey;
import ftl.ast.DynamicKey;
import ftl.ast.EqualityExpression;
import ftl.ast.Exists;
import ftl.ast.Expression;
import ftl.ast.HashLiteral;
import ftl.ast.ListLiteral;
import ftl.ast.MethodInvoke;
import ftl.ast.MultiplicativeExpression;
import ftl.ast.NotExpression;
import ftl.ast.NullLiteral;
import ftl.ast.NumberLiteral;
import ftl.ast.OrExpression;
import ftl.ast.ParameterList;
import ftl.ast.Parenthesis;
import ftl.ast.PositionalArgsList;
import ftl.ast.PrimaryExpression;
import ftl.ast.RangeExpression;
import ftl.ast.RelationalExpression;
import ftl.ast.StringLiteral;
import ftl.ast.UnaryPlusMinusExpression;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ParameterListBuilderTest {

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
        ArrayList<ParameterHolder> parameterHolders = new ArrayList<>();
        assertThrows(ParsingException.class, () -> parameterList.accept(ParameterListBuilder.INSTANCE, parameterHolders));
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

    @Test
    void unsupportedVisits() {
        assertThrows(UnsupportedOperationException.class, () -> ParameterListBuilder.INSTANCE.visit((Node) null, null));
        assertThrows(UnsupportedOperationException.class, () -> ParameterListBuilder.INSTANCE.visit((Expression) null, null));
        assertThrows(UnsupportedOperationException.class, () -> ParameterListBuilder.INSTANCE.visit((OrExpression) null, null));
        assertThrows(UnsupportedOperationException.class, () -> ParameterListBuilder.INSTANCE.visit((AndExpression) null, null));
        assertThrows(UnsupportedOperationException.class, () -> ParameterListBuilder.INSTANCE.visit((EqualityExpression) null, null));
        assertThrows(UnsupportedOperationException.class, () -> ParameterListBuilder.INSTANCE.visit((RelationalExpression) null, null));
        assertThrows(UnsupportedOperationException.class, () -> ParameterListBuilder.INSTANCE.visit((RangeExpression) null, null));
        assertThrows(UnsupportedOperationException.class, () -> ParameterListBuilder.INSTANCE.visit((MultiplicativeExpression) null, null));
        assertThrows(UnsupportedOperationException.class, () -> ParameterListBuilder.INSTANCE.visit((UnaryPlusMinusExpression) null, null));
        assertThrows(UnsupportedOperationException.class, () -> ParameterListBuilder.INSTANCE.visit((NotExpression) null, null));
        assertThrows(UnsupportedOperationException.class, () -> ParameterListBuilder.INSTANCE.visit((PrimaryExpression) null, null));
        assertThrows(UnsupportedOperationException.class, () -> ParameterListBuilder.INSTANCE.visit((DefaultToExpression) null, null));
        assertThrows(UnsupportedOperationException.class, () -> ParameterListBuilder.INSTANCE.visit((BaseExpression) null, null));
        assertThrows(UnsupportedOperationException.class, () -> ParameterListBuilder.INSTANCE.visit((NumberLiteral) null, null));
        assertThrows(UnsupportedOperationException.class, () -> ParameterListBuilder.INSTANCE.visit((HashLiteral) null, null));
        assertThrows(UnsupportedOperationException.class, () -> ParameterListBuilder.INSTANCE.visit((StringLiteral) null, null));
        assertThrows(UnsupportedOperationException.class, () -> ParameterListBuilder.INSTANCE.visit((BooleanLiteral) null, null));
        assertThrows(UnsupportedOperationException.class, () -> ParameterListBuilder.INSTANCE.visit((NullLiteral) null, null));
        assertThrows(UnsupportedOperationException.class, () -> ParameterListBuilder.INSTANCE.visit((ListLiteral) null, null));
        assertThrows(UnsupportedOperationException.class, () -> ParameterListBuilder.INSTANCE.visit((Parenthesis) null, null));
        assertThrows(UnsupportedOperationException.class, () -> ParameterListBuilder.INSTANCE.visit((BuiltinVariable) null, null));
        assertThrows(UnsupportedOperationException.class, () -> ParameterListBuilder.INSTANCE.visit((DotKey) null, null));
        assertThrows(UnsupportedOperationException.class, () -> ParameterListBuilder.INSTANCE.visit((DynamicKey) null, null));
        assertThrows(UnsupportedOperationException.class, () -> ParameterListBuilder.INSTANCE.visit((MethodInvoke) null, null));
        assertThrows(UnsupportedOperationException.class, () -> ParameterListBuilder.INSTANCE.visit((BuiltIn) null, null));
        assertThrows(UnsupportedOperationException.class, () -> ParameterListBuilder.INSTANCE.visit((Exists) null, null));
        assertThrows(UnsupportedOperationException.class, () -> ParameterListBuilder.INSTANCE.visit((PositionalArgsList) null, null));
    }
}