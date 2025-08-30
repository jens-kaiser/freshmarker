package org.freshmarker.core.ftl;

import ftl.Token.TokenType;
import ftl.ast.MacroDefinition;
import org.freshmarker.Template;
import org.freshmarker.api.FeatureSet;
import org.freshmarker.core.StaticContext;
import org.freshmarker.core.directive.MacroUserDirective;
import org.freshmarker.core.environment.NameSpaced;
import org.freshmarker.core.fragment.ConstantFragment;
import org.freshmarker.core.fragment.Fragment;
import org.freshmarker.core.fragment.Fragments;
import org.freshmarker.core.ftl.TemplateDictionary.VariableType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MacroBuilder implements UnaryFtlVisitor<List<Fragment>> {

    private static final Logger logger = LoggerFactory.getLogger(MacroBuilder.class);

    private final Template template;
    private final String nameSpace;
    private final ParameterListBuilder parameterListBuilder;
    private final TemplateDictionary dictionary = new TemplateDictionary();
    private final FragmentBuilder fragmentBuilder;

    public MacroBuilder(Template template, String nameSpace, FeatureSet featureSet, int includeLevel, StaticContext templateContext) {
        this.template = template;
        this.nameSpace = nameSpace;
        parameterListBuilder = new ParameterListBuilder(new InterpolationBuilder(featureSet, templateContext, dictionary));
        fragmentBuilder = new FragmentBuilder(template, nameSpace, featureSet, includeLevel, templateContext, dictionary);
    }

    @Override
    public List<Fragment> visit(MacroDefinition ftl, List<Fragment> input) {
        TokenType type = (TokenType) ftl.get(1).getType();
        if (type != TokenType.MACRO) {
            throw new ParsingException("function unsupported", ftl);
        }
        String name = ftl.get(3).toString();
        List<ParameterHolder> parameterList = getParameterHolders(ftl);
        for (ParameterHolder parameterHolder : parameterList) {
            dictionary.putVariable(parameterHolder.name(), VariableType.ARG);
        }
        Fragment block = getFragment(ftl);
        logger.debug("macro directive: namespace={}, type={}, name={}, block={}", nameSpace, type, name, block);
        template.getUserDirectives().put(new NameSpaced(nameSpace, name), new MacroUserDirective(block, parameterList));
        return input;
    }

    private Fragment getFragment(MacroDefinition ftl) {
        if (ftl.get(ftl.size() - 2).getType() == TokenType.CLOSE_TAG) {
            return ConstantFragment.EMPTY;
        }
        return Fragments.optimizeWithVariableContext(ftl.get(ftl.size() - 2).accept(fragmentBuilder, new ArrayList<>()));
    }

    private List<ParameterHolder> getParameterHolders(MacroDefinition ftl) {
        int parameterListIndex = getParameterListIndex(ftl);
        if (ftl.get(parameterListIndex).getType() == TokenType.CLOSE_TAG) {
            return Collections.emptyList();
        }
        return ftl.get(parameterListIndex).accept(parameterListBuilder, new ArrayList<>());
    }

    private int getParameterListIndex(MacroDefinition ftl) {
        int closeTag = ftl.indexOf(ftl.firstChildOfType(TokenType.CLOSE_TAG));
        if (closeTag == 4) {
            return 4;
        }
        return ftl.firstChildOfType(TokenType.OPEN_PAREN) == null ? 4 : 5;
    }
}
