package org.freshmarker.core.ftl;

import ftl.FreshMarkerParser;
import ftl.ParseException;
import ftl.ast.Root;
import org.freshmarker.core.model.TemplateJunction;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.model.TemplateVariable;
import org.freshmarker.core.model.primitive.TemplateBoolean;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class ExpressionOptimizationTest {

    @ParameterizedTest
    @CsvSource({
            "${true},true",
            "${false},false",
            "${!true},false",
            "${!false},true",
            "${true & true},true",
            "${true & false},false",
            "${false & true},false",
            "${false & false},false",
            "${true && true},true",
            "${true && false},false",
            "${false && true},false",
            "${false && false},false",
            "${false && variable},false",
            "${!(false && variable)},true",
            "${true | true},true",
            "${true | false},true",
            "${false | true},true",
            "${false | false},false",
            "${true || true},true",
            "${true || false},true",
            "${false || true},true",
            "${false || false},false",
            "${true || variable},true",
            "${!(true || variable)},false",
            "${true ^ true},false",
            "${true ^ false},true",
            "${false ^ true},true",
            "${false ^ false},false",
    })
    void optimizeWithBooleanConstants(String content, boolean expected) throws ParseException {
        FreshMarkerParser parser = new FreshMarkerParser(content);
        parser.setInputSource("test");
        parser.Root();
        Root root = (Root) parser.rootNode();
        TemplateObject templateObject = root.getFirst().get(1).accept(new InterpolationBuilder(null, null, new TemplateDictionary()), null);
        TemplateBoolean templateBoolean = assertInstanceOf(TemplateBoolean.class, templateObject);
        assertEquals(expected, templateBoolean.getValue());
    }

    @ParameterizedTest
    @CsvSource({
            "${variable}",
            "${variable & true}",
            "${true & variable}",
            "${variable && true}",
            "${variable | false}",
            "${variable || false}",
            "${false || variable}",
            "${false | variable}",
    })
    void optimizeExpressionWithVariable(String content) throws ParseException {
        FreshMarkerParser parser = new FreshMarkerParser(content);
        parser.setInputSource("test");
        parser.Root();
        Root root = (Root) parser.rootNode();
        TemplateObject templateObject = root.getFirst().get(1).accept(new InterpolationBuilder(null, null, new TemplateDictionary()), null);
        TemplateVariable templateVariable = assertInstanceOf(TemplateVariable.class, templateObject);
        assertEquals("variable", templateVariable.name());
    }

    @ParameterizedTest
    @CsvSource({
            "${false ^ variable}",
            "${variable ^ false}",
            "${false ^ variable}",
            "${false & variable},false",
            "${variable & false},false",
            "${true | variable},true",
            "${variable | true},true",
    })
    void notOptimizeExpressionWithVariable(String content) throws ParseException {
        FreshMarkerParser parser = new FreshMarkerParser(content);
        parser.setInputSource("test");
        parser.Root();
        Root root = (Root) parser.rootNode();
        TemplateObject templateObject = root.getFirst().get(1).accept(new InterpolationBuilder(null, null, new TemplateDictionary()), null);
        assertInstanceOf(TemplateJunction.class, templateObject);
    }
}
