package cz.meind.synchro.synchrobackend.service;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final HandlerExceptionResolver handlerExceptionResolver;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService, HandlerExceptionResolver handlerExceptionResolver) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.handlerExceptionResolver = handlerExceptionResolver;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        try {


            // Check if the request is a POST request with the "Authorization" header
            // Extract the JWT token from cookies
            String jwt = extractTokenFromCookies(request);

            // Check if the JWT token is present
            if (jwt != null) {
                final String userEmail = jwtService.extractUsername(jwt);
                if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

                    // Validate the JWT token
                    if (jwtService.isTokenValid(jwt, userDetails)) {
                        // Set authentication in the security context
                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                        String requestURI = request.getRequestURI();
                        if (requestURI.equals("/login.html") || requestURI.equals("/signup.html")) {
                            response.sendRedirect("/index.html");
                            return;
                        }
                    }
                }
            }

            // If no JWT is present, continue to the requested resource
            filterChain.doFilter(request, response);
            System.out.println(response.getStatus());
        } catch (Exception exception) {
            handlerExceptionResolver.resolveException(request, response, null, exception);
        }
    }


    /**
     * Extracts the JWT token from cookies in the request.
     * It expects the cookie in the format: token=token_number
     */
    private String extractTokenFromCookies(HttpServletRequest request) {
        if (request.getCookies() != null) {
            Optional<Cookie> tokenCookie = Arrays.stream(request.getCookies()).filter(cookie -> "token".equals(cookie.getName()))  // Find cookie with name "token"
                    .findFirst();
            if (tokenCookie.isPresent()) {
                // Extract the token value from the cookie
                return tokenCookie.get().getValue();

            }
        }
        return null;  // Return null if no valid token is found
    }
}