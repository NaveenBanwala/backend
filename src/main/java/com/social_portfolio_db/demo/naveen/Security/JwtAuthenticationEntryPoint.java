package com.social_portfolio_db.demo.naveen.Security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

// @Component
// public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

//     @Override
//     public void commence(
//             HttpServletRequest request,
//             HttpServletResponse response,
//             AuthenticationException authException
//     ) throws IOException, ServletException {

//         // This is invoked when a user tries to access a secured REST resource without supplying any credentials
//         response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized: Invalid or missing token");
//     }

@Slf4j
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                        AuthenticationException authException) throws IOException {
        log.error("Unauthorized error: {}", authException.getMessage());
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized: " + authException.getMessage());
    }
}


