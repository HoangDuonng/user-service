package com.graduationproject.user_service.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class ServiceAuthenticationFilter extends OncePerRequestFilter {

    private static final String SERVICE_TOKEN = "Bearer service-auth-2024-secret-key";
    private static final String SERVICE_ROLE = "SERVICE";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        // Kiểm tra nếu có Authorization header và đúng service token
        if (authHeader != null && authHeader.equals(SERVICE_TOKEN)) {
            // Tạo authentication với role SERVICE
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    "service",
                    null,
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + SERVICE_ROLE)));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);
        } else {
            // Nếu không phải service token, chuyển cho filter tiếp theo (JWT filter)
            filterChain.doFilter(request, response);
        }
    }
}
