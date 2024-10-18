package org.freshmarker.core.model.version;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record Version(int major, int minor, int patch) implements Comparable<Version> {

    public static final Pattern PATTERN = Pattern.compile("^(\\d+)\\.(\\d+)\\.(\\d+)(-.*)?$");

    public static Version byString(String value) {
        Matcher matcher = PATTERN.matcher(value);
        if (!matcher.matches()) {
            throw new IllegalStateException("cannot parse version:" + value);
        }
        return new Version(getParseInt(matcher, 1), getParseInt(matcher, 2), getParseInt(matcher, 3));
    }

    private static int getParseInt(Matcher matcher, int group) {
        return Integer.parseInt(matcher.group(group));
    }

    @Override
    public String toString() {
        return major + "." + minor + "." + patch;
    }

    @Override
    public int compareTo(Version version) {
        int majorCompare = Integer.compare(major, version.major);
        if (majorCompare != 0) {
            return majorCompare;
        }
        int minorCompare = Integer.compare(minor, version.minor);
        if (minorCompare != 0) {
            return minorCompare;
        }
        return Integer.compare(patch, version.patch);
    }
}
