package org.freshmarker.core;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Deprecated
public class ModelSecurityGateway {
    private final Set<String> allowedPackages = new HashSet<>();
    private final Set<String> allowedClasses = new HashSet<>();

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

    public interface ModelSecurityHandler {
        void check(Class<?> type);
    }

    public record SimpleModelSecurityHandler(Set<String> allowedPackages, Set<String> allowedClasses, Set<String> forbiddenPackages, Set<String> checked)
            implements ModelSecurityHandler {
        public void check(Class<?> type) {
            if (checked.contains(type.getName())) {
                return;
            }
            boolean isForbiddenPackage = isForbiddenPackage(type);
            if (!isForbiddenPackage) {
                checked.add(type.getName());
                return;
            }
            boolean isAllowedClass = allowedClasses.contains(type.getName());
            if (isAllowedClass) {
                checked.add(type.getName());
                return;
            }
            boolean isAllowedPackage = isAllowedPackage(type);
            if (isAllowedPackage) {
                checked.add(type.getName());
                return;
            }
            throw new UnsupportedDataTypeException("unsupported system class: " + type);
        }

        private boolean isAllowedPackage(Class<?> type) {
            return isPackage(allowedPackages, type.getName());
        }

        private boolean isForbiddenPackage(Class<?> type) {
            return isPackage(forbiddenPackages, type.getName());
        }

        private boolean isPackage(Set<String> packages, String name) {
            for (String forbidden : packages) {
                if (name.startsWith(forbidden)) {
                    return true;
                }
            }
            return false;
        }
    }

    public ModelSecurityHandler build() {
        return new SimpleModelSecurityHandler(Set.copyOf(allowedPackages), Set.copyOf(allowedClasses), Set.copyOf(forbiddenPackages), new HashSet<>());
    }
}
