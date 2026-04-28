package com.vendora.epic1.security;

import com.vendora.epic1.service.SystemSetupService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.List;

@Component
public class SetupInterceptor implements HandlerInterceptor {

    private final SystemSetupService setupService;

    // Paths that are always allowed even in setup mode
    private static final List<String> ALLOWED_PATHS = List.of(
            "/admin-signup",
            "/api/auth/register/admin",
            "/css/",
            "/js/",
            "/images/",
            "/assets/",
            "/html/",
            "/error",
            "/favicon.ico"
    );

    public SetupInterceptor(SystemSetupService setupService) {
        this.setupService = setupService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (setupService.isLaunched()) {
            return true;
        }

        String path = request.getRequestURI();

        // Check if path is in the allowed list
        for (String allowedPath : ALLOWED_PATHS) {
            if (path.startsWith(allowedPath) || path.equals(allowedPath)) {
                return true;
            }
        }

        // If it's an API request, return 403
        if (path.startsWith("/api/")) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            response.getWriter().write("{\"success\":false,\"message\":\"Platform is in Setup Mode. Awaiting Admin Launch.\"}");
            return false;
        }

        // For all other web requests, redirect to the admin-signup page
        response.sendRedirect("/admin-signup");
        return false;
    }
}
