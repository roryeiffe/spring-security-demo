package com.revature.springsecuritydemo.config;

import com.revature.springsecuritydemo.services.JwtUserDetailsService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// this parsing/intercepting incoming requests and making sure they have a valid JWT token
// for example, trying to access Hello Controller should ONLY be allowed if there is a JWT present in the headers
@Component
public class JwtRequestFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // any user who wants to authenticate themselves must pass in JWT in the header with key "Authorization"
        final String requestTokenHeader = request.getHeader("Authorization");

        String username = null, jwtToken = null;

        // if token is non-null and starts with "Bearer":
        if(requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            // removing the word "Bearer" so we just get the token itself:
            jwtToken = requestTokenHeader.substring(7);
            // try to get username from token but printing out according messages if an excpetion is thrown:
            try {
                username = jwtTokenUtil.getUsernameFromToken(jwtToken);
            } catch (IllegalArgumentException e) {
                System.out.println("Unable to get JWT Token");
            } catch (ExpiredJwtException e) {
                System.out.println("JWT Token has expired");
            }
        }


        // once we have the token, we can validate it:
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(username);

            // validate our token using the token util:
            if(jwtTokenUtil.validateToken(jwtToken, userDetails)) {
                // configure our security context accordingly:
                UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                token.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                // I forgot this **VERY** important line which actually sets the token to the security context
                // Otherwise, we don't actually let Spring security know that this request is allowed due to the token being valid
                SecurityContextHolder.getContext().setAuthentication(token);
            }
            else {
                System.out.println("Ivalid Token!");
            }
        }
        // either way, we move on with the request
        filterChain.doFilter(request, response);

    }
}
