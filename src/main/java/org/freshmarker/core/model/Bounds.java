package org.freshmarker.core.model;

import org.freshmarker.core.ProcessException;

public record Bounds(int lower, int upper) {
    int safeOffset(int index) {
        return lower < upper ? lower + index : lower - index;
    }

    int offset(int index) {
        int result = safeOffset(index);
        if (lower < upper && result > upper) {
            throw new IndexOutOfBoundsException(index);
        }
        if (lower > upper && result < upper) {
            throw new IndexOutOfBoundsException(index);
        }
        return result;
    }

    public Bounds intersect(Bounds bounds) {
        if (bounds.lower() < 0 || bounds.upper() < 0) {
            throw new ProcessException("negative slicing values not allowed: " + bounds);
        }
        int newLower = offset(bounds.lower());
        int newUpper = offset(bounds.upper());
        if (lower < upper) {
            return new Bounds(Math.min(newLower, upper), Math.min(newUpper, upper));
        }
        return new Bounds(Math.max(newLower, upper), Math.max(newUpper, upper));
    }

    public Bounds intersect(int lowerBound) {
        if (lowerBound < 0) {
            throw new ProcessException("negative slicing values not allowed: " + lowerBound);
        }
        int newLower = offset(lowerBound);
        return new Bounds(lower < upper ? Math.min(newLower, upper) : Math.max(newLower, upper), upper);
    }
}
