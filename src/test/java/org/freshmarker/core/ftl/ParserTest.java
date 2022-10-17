package org.freshmarker.core.ftl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ftl.FTLConstants.TokenType;
import ftl.FTLParser;
import ftl.ParseException;
import ftl.ast.Root;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import org.freshmarker.Template;
import org.freshmarker.core.InterpolationListener;
import org.freshmarker.core.model.TemplateEquality;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.model.TemplateRelational;
import org.freshmarker.core.model.number.CalculatingNumber;
import org.freshmarker.core.model.primitive.TemplateBoolean;
import org.freshmarker.core.model.primitive.TemplateNumber;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ParserTest {

  private Map<String, TemplateObject> interpolations;
  private InterpolationListener interpolationListener;

  @BeforeEach
  public void setUp() {
    interpolations = new HashMap<>();
    interpolationListener = new InterpolationListener() {
      @Override
      public void evaluatedInterpolation(String formular, TemplateObject expression) {
        interpolations.put(formular, expression);
      }
    };
  }

  @Test
  void multipleNegatives() throws ParseException {
    FTLParser parser = new FTLParser(new StringReader("${-(-(-42))}"));
    parser.Root();
    Root root = (Root) parser.rootNode();
    Template template = new Template(null);
    root.accept(new FragmentBuilder(template, interpolationListener), template.getRootFragment());
    TemplateObject templateObject = interpolations.get("${-(-(-42))}");
    assertNotNull(templateObject);
    assertEquals(-42,
        templateObject.asNumber().map(TemplateNumber::getValue).map(CalculatingNumber::getNumber).orElseThrow());
  }

  @Test
  void multipleNots() throws ParseException {
    FTLParser parser = new FTLParser(new StringReader("${!(!(!false))}"));
    parser.Root();
    Root root = (Root) parser.rootNode();
    Template template = new Template(null);
    root.accept(new FragmentBuilder(template, interpolationListener), template.getRootFragment());
    TemplateObject templateObject = interpolations.get("${!(!(!false))}");
    assertNotNull(templateObject);
    assertEquals(TemplateBoolean.TRUE, templateObject);
  }

  @Test
  void negatedRelational() throws ParseException {
    FTLParser parser = new FTLParser(new StringReader("${!(a <= b)}"));
    parser.Root();
    Root root = (Root) parser.rootNode();
    Template template = new Template(null);
    root.accept(new FragmentBuilder(template, interpolationListener), template.getRootFragment());
    TemplateObject templateObject = interpolations.get("${!(a <= b)}");
    assertNotNull(templateObject);
    assertTrue(templateObject instanceof TemplateRelational);
    TemplateRelational templateRelational = (TemplateRelational) templateObject;
    assertEquals(TokenType.GT, templateRelational.getType());
  }

  @Test
  void equal() throws ParseException {
    FTLParser parser = new FTLParser(new StringReader("${!(a != b)}"));
    parser.Root();
    Root root = (Root) parser.rootNode();
    Template template = new Template(null);
    root.accept(new FragmentBuilder(template, interpolationListener), template.getRootFragment());
    TemplateObject templateObject = interpolations.get("${!(a != b)}");
    assertNotNull(templateObject);
    assertTrue(templateObject instanceof TemplateEquality);
  }
}
