package org.freshmarker.core.fragment;

import ftl.ParseException;
import ftl.Token.TokenType;

public enum RelationType {
    LT, LTE, GTE, GT;

    public static RelationType from(TokenType type) {
        return switch (type) {
            case LT -> LT;
            case LTE -> LTE;
            case GTE -> GTE;
            case GT -> GT;
            default -> throw new ParseException("invalid relation: " + type);
        };
    }

    public boolean compare(int compare) {
        return switch (this) {
            case LT -> compare < 0;
            case GT -> compare > 0;
            case LTE -> compare <= 0;
            case GTE -> compare >= 0;
        };
    }
}
