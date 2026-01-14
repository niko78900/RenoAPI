package com.example.HomeReno.security;

import com.example.HomeReno.entity.Project;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtils {
    private SecurityUtils() {}

    public static UserPrincipal requireUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserPrincipal principal)) {
            throw new AccessDeniedException("Authentication required");
        }
        return principal;
    }

    public static void requireProjectAccess(Project project) {
        UserPrincipal principal = requireUser();
        if (principal.isAdmin()) {
            return;
        }
        String ownerId = project.getOwnerId();
        if (ownerId == null || !ownerId.equals(principal.getId())) {
            throw new AccessDeniedException("Access denied");
        }
    }
}
