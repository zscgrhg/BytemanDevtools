package com.example.tools.byteman.beans;

import java.security.Permission;

public class BytemanSecurityManager extends SecurityManager {
    private final SecurityManager baseSecurityManager;

    public BytemanSecurityManager(SecurityManager baseSecurityManager) {
        this.baseSecurityManager = baseSecurityManager;
    }


    @Override
    public void checkPermission(Permission permission) {
        if (permission.getName().startsWith("exitVM")) {
            StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
            for (StackTraceElement element : stackTrace) {
                if (element.getClassName().startsWith("org.jboss.byteman.agent")) {
                    throw new SecurityException("System exit not allowed");
                }
            }
        }
        if (baseSecurityManager != null) {
            baseSecurityManager.checkPermission(permission);
        }
    }
}
