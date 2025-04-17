package org.freshmarker.core.utils;

import java.time.LocalDate;
import java.util.stream.IntStream;

public class EasterCache {
    static final int MIN = 2000;
    static final int MAX = 2100;

    static final LocalDate[] cache;

    static {
        cache = IntStream.range(MIN, MAX).mapToObj(year -> gauss2(year, 5, 24)).toArray(LocalDate[]::new);
    }

    private EasterCache() {
        super();
    }

    public static LocalDate gauss(int year) {
        if (year < EasterCache.MIN || year >= EasterCache.MAX) {
            return EasterCache.gauss2(year);
        }
        return EasterCache.cache[year - EasterCache.MIN];
    }

    static LocalDate gauss2(int year) {
        int h1 = year / 100;
        int h2 = year / 400;
        int n = 4 + h1 - h2;
        int m = 15 + h1 - h2 - (8 * h1 + 13) / 25;
        return gauss2(year, n, m);
    }

    static LocalDate gauss2(int year, int n, int m) {
        int a = year % 19;
        int b = year % 4;
        int c = year % 7;
        int d = (19 * a + m) % 30;
        int e = (2 * b + 4 * c + 6 * d + n) % 7;
        int f = (c + 11 * d + 22 * e) / 451;
        int tage = 22 + d + e - 7 * f;
        return LocalDate.of(year, 3, 1).plusDays(tage - 1L);
    }
}