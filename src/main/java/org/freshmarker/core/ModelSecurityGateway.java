package org.freshmarker.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ModelSecurityGateway {
    private final List<String> allowedPackages = new ArrayList<>();
    private final List<String> allowedClasses = new ArrayList<>();

    private final Set<String> forbiddenPackages = new HashSet<>();

    public void addForbiddenPackages(String... packageName) {
        forbiddenPackages.addAll(Arrays.stream(packageName).map(p -> p.endsWith(".") ? p : p + ".").toList());
    }

    public void addForbiddenPackages(Class<?> type) {
        addForbiddenPackages(type.getPackageName());
    }

    public void addAllowedClass(Class<?> type) {
        allowedClasses.add(type.getName());
    }

    public void addAllowedPackages(String... packageName) {
        allowedPackages.addAll(Arrays.stream(packageName).map(p -> p.endsWith(".") ? p : p + ".").toList());
    }

    public void addAllowedPackages(Class<?> type) {
        addAllowedPackages(type.getPackageName());
    }

    public void check(Class<?> type) {
        boolean isForbiddenPackage = forbiddenPackages.stream().anyMatch(s -> type.getName().startsWith(s));
        if (!isForbiddenPackage) {
            return;
        }
        boolean isAllowedClass = allowedClasses.contains(type.getName());
        if (isAllowedClass) {
            return;
        }
        boolean isAllowedPackage = allowedPackages.stream().anyMatch(s -> type.getName().startsWith(s));
        if (isAllowedPackage) {
            return;
        }
        throw new UnsupportedDataTypeException("unsupported system class: " + type);
    }
}
