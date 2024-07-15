package org.freshmarker.core.model.version;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record Version(int major, int minor, int patch) {

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

    public boolean isBefore(Version version) {
        if (major() != version.major()) {
            return major() < version.major();
        }
        if (minor() != version.minor()) {
            return minor() < version.minor();
        }
        if (patch() != version.patch()) {
            return patch() < version.patch();
        }
        return false;
    }

    public boolean isAfter(Version version) {
        if (major() != version.major()) {
            return major() > version.major();
        }
        if (minor() != version.minor()) {
            return minor() > version.minor();
        }
        if (patch() != version.patch()) {
            return patch() > version.patch();
        }
        return false;
    }

    @Override
    public String toString() {
        return major + "." + minor + "." + patch;
    }
}
