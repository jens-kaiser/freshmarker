package org.freshmarker.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.File;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class ModelSecurityGatewayTest {
    private ModelSecurityGateway modelSecurityGateway;

    @BeforeEach
    void setUp() {
        modelSecurityGateway = new ModelSecurityGateway();
    }

    @ParameterizedTest
    @CsvSource( {"java.io", "java.io."})
    void addForbiddenPackages(String forbiddenPackage) {
        assertDoesNotThrow(() -> modelSecurityGateway.check(File.class));
        modelSecurityGateway.addForbiddenPackages(forbiddenPackage);
        assertThrows(UnsupportedDataTypeException.class, () -> modelSecurityGateway.check(File.class));
    }

    @Test
    void addForbiddenPackagesByClass() {
        assertDoesNotThrow(() -> modelSecurityGateway.check(File.class));
        modelSecurityGateway.addForbiddenPackages(File.class);
        assertThrows(UnsupportedDataTypeException.class, () -> modelSecurityGateway.check(File.class));
    }

    @Test
    void addAllowedClass() {
        modelSecurityGateway.addForbiddenPackages("java.io");
        assertThrows(UnsupportedDataTypeException.class, () -> modelSecurityGateway.check(File.class));
        modelSecurityGateway.addAllowedClass(File.class);
        assertDoesNotThrow(() -> modelSecurityGateway.check(File.class));
    }

    @ParameterizedTest
    @CsvSource( {"java.nio,java.nio.file", "java.nio.,java.nio.file.","java.nio.,java.nio.file", "java.nio.,java.nio.file"})
    void addAllowedPackages(String forbiddenPackage, String allowedPackage) {
        modelSecurityGateway.addForbiddenPackages(forbiddenPackage);
        assertThrows(UnsupportedDataTypeException.class, () -> modelSecurityGateway.check(Path.class));
        modelSecurityGateway.addAllowedPackages(allowedPackage);
        assertDoesNotThrow(() -> modelSecurityGateway.check(Path.class));
    }

    @Test
    void addAllowedPackagesByClass() {
        modelSecurityGateway.addForbiddenPackages("java.nio");
        assertThrows(UnsupportedDataTypeException.class, () -> modelSecurityGateway.check(Path.class));
        modelSecurityGateway.addAllowedPackages(Path.class);
        assertDoesNotThrow(() -> modelSecurityGateway.check(Path.class));
    }
}