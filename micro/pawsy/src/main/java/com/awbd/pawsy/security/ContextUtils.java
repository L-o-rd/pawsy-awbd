package com.awbd.pawsy.security;

import org.springframework.security.core.context.SecurityContextHolder;
import static java.util.Objects.requireNonNull;

public final class ContextUtils {
    public static String getCurrentUsername() {
        var auth = requireNonNull(SecurityContextHolder.getContext().getAuthentication());
        return auth.getName();
    }
}
