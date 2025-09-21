package org.freshmarker.core.model;

import ftl.Token.TokenType;
import org.freshmarker.core.ProcessContext;
import org.freshmarker.core.model.TemplateOperation.Operator;
import org.freshmarker.core.model.TemplateRelational.Relation;
import org.freshmarker.core.model.primitive.TemplateString;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class TemplateObjectDeprecationsTest {

    @ParameterizedTest
    @CsvSource({
            "PLUS,PLUS",
            "MINUS,MINUS",
            "TIMES,MULTIPLY",
            "DIVIDE,DIVIDE",
            "PERCENT,MODULO",
            "CONCAT,CONCAT",
    })
    void operation(TokenType type, String expected) {
        TemplateObject deprecated = new TemplateObject() {
            @Override
            public TemplateObject evaluateToObject(ProcessContext context) {
                return null;
            }

            @Override
            public TemplateObject operation(Operator operator, TemplateObject operand, ProcessContext context) {
                return new TemplateString(operator.toString());
            }
        };
        assertEquals(expected, deprecated.operation(type, null, null).toString());
    }

    @ParameterizedTest
    @CsvSource({
            "LT",
            "GT",
            "LTE",
            "GTE",
            "UNICODE_GTE",
    })
    void relation(TokenType type) {
        TemplateObject deprecated = new TemplateObject() {
            @Override
            public TemplateObject evaluateToObject(ProcessContext context) {
                return null;
            }

            @Override
            public boolean relation(Relation operator, TemplateObject operand, ProcessContext context) {
                return true;
            }
        };
        assertTrue(deprecated.relation(type, null, null));
    }
}