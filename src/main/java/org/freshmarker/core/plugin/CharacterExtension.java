package org.freshmarker.core.plugin;

import org.freshmarker.api.BuiltIn;
import org.freshmarker.api.extension.BuiltInProvider;
import org.freshmarker.api.extension.Register;
import org.freshmarker.api.extension.support.SingleTypeBuiltInRegister;
import org.freshmarker.core.model.TemplateObject;
import org.freshmarker.core.model.primitive.TemplateBoolean;
import org.freshmarker.core.model.primitive.TemplateCharacter;
import org.freshmarker.core.model.primitive.TemplateString;

import java.lang.Character.UnicodeBlock;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

public class CharacterExtension implements BuiltInProvider {
    @Override
    public Register<Class<? extends TemplateObject>, String, BuiltIn> provideBuiltInRegister() {
        SingleTypeBuiltInRegister builtInRegister = new SingleTypeBuiltInRegister(TemplateCharacter.class);
        builtInRegister.add("c", BuiltIn.string());
        builtInRegister.add("unicode_block", (x, y, e) -> getUnicodeBlock((TemplateCharacter) x));
        builtInRegister.add("is_whitespace", (x, y, e) -> apply((TemplateCharacter) x, Character::isWhitespace));
        builtInRegister.add("is_digit", (x, y, e) -> apply((TemplateCharacter) x, Character::isDigit));
        builtInRegister.add("is_alphabetic", (x, y, e) -> apply((TemplateCharacter) x, Character::isAlphabetic));
        builtInRegister.add("is_emoji", (x, y, e) -> apply((TemplateCharacter) x, Character::isEmoji));
        builtInRegister.add("is_letter", (x, y, e) -> apply((TemplateCharacter) x, Character::isLetter));
        builtInRegister.add("is_lower_case", (x, y, e) -> apply((TemplateCharacter) x, Character::isLowerCase));
        builtInRegister.add("is_upper_case", (x, y, e) -> apply((TemplateCharacter) x, Character::isUpperCase));
        builtInRegister.add("lower_case", (x, y, e) -> convert((TemplateCharacter) x, Character::toLowerCase));
        builtInRegister.add("upper_case", (x, y, e) -> convert((TemplateCharacter) x, Character::toUpperCase));
        builtInRegister.add("is_character", BuiltInHelper.alwaysTrue());
        return builtInRegister;
    }

    private static TemplateString getUnicodeBlock(TemplateCharacter x) {
        return new TemplateString(UnicodeBlock.of(x.getValue()).toString());
    }

    private static TemplateBoolean apply(TemplateCharacter x, Predicate<Character> predicate) {
        return TemplateBoolean.from(predicate.test(x.getValue()));
    }

    private static TemplateCharacter convert(TemplateCharacter x, UnaryOperator<Character> operator) {
        return new TemplateCharacter(operator.apply(x.getValue()));
    }
}
