    package com.clinicaapp.ClinicaApp.Security;

    import com.clinicaapp.ClinicaApp.Exeption.CustomAuthenticationEntryPoint;
    import jakarta.servlet.FilterChain;
    import jakarta.servlet.ServletException;
    import jakarta.servlet.http.HttpServletRequest;
    import jakarta.servlet.http.HttpServletResponse;
    import lombok.RequiredArgsConstructor;
    import lombok.extern.slf4j.Slf4j;
    import org.springframework.security.authentication.BadCredentialsException;
    import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
    import org.springframework.security.core.context.SecurityContextHolder;
    import org.springframework.security.core.userdetails.UserDetails;
    import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
    import org.springframework.stereotype.Component;
    import org.springframework.web.filter.OncePerRequestFilter;

    import org.springframework.security.core.AuthenticationException;
    import java.io.IOException;

    @Component
    @Slf4j
    @RequiredArgsConstructor
    public class AuthFilter extends OncePerRequestFilter {

        private final JWTService tokenService;
        private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
        private final CustomUserDetailsService customUserDetailsService;



        @Override
        protected void doFilterInternal(HttpServletRequest request,
                                        HttpServletResponse response,
                                        FilterChain filterChain)
                throws ServletException, IOException {

            String token = getTokenFromRequest(request);

            if (token != null){
                String email;
                try {
                    email = tokenService.getUsernameFromToken(token);
                } catch (Exception e) {
                    log.error("Exception occured while extracting username from token");
                    AuthenticationException authenticationException = new BadCredentialsException(e.getMessage());
                    customAuthenticationEntryPoint.commence(request, response, authenticationException);
                    return;
                }

// 🔥 AQUI ESTÁ O QUE FALTA
                if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                    UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);

                    if (tokenService.isTokenValid(token, userDetails)) {

                        UsernamePasswordAuthenticationToken authToken =
                                new UsernamePasswordAuthenticationToken(
                                        userDetails,
                                        null,
                                        userDetails.getAuthorities()
                                );

                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    }
                }
            }
            filterChain.doFilter(request, response);

        }

        private String getTokenFromRequest(HttpServletRequest request){
            String tokenWithBearer = request.getHeader("Authorization");
            if(tokenWithBearer != null && tokenWithBearer.startsWith("Bearer ")){
                return tokenWithBearer.substring(7);
            }
            return null;
        }

    }
