package com.revature.springsecuritydemo.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/*
This class is responsible for creating/managing/verifying
including setting up the expiration date
 */
@Component
public class JwtTokenUtil {
    // 1 hour converted to seconds
    public static final long JWT_TOKEN_VALIDITY = 1 * 60 * 60;

    @Value("${jwt.secret}")
    private String secret;

    // generate token based on user details (username and password)
    public String generateToken(UserDetails userDetails) {
        // claims is used to store values like issued at time, username
        Map<String, Object> claims = new HashMap<>();
        String username = userDetails.getUsername();
        return Jwts.builder().setClaims(claims)
                // set the username as the subject:
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                // convert seconds to milliseconds based on final variable
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
                .signWith(SignatureAlgorithm.HS512, secret).compact();
    }

    // getting claims from the token:
    // using the JWT parser:
    private Claims getClaimsForToken(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }

    // get a specific claim from a token, could be any type, so we use generics
    // we can pass in for example, a username resolver to get the username
    public <T> T getClaimForToken(String token, Function<Claims, T> claimsResolver) {
        Claims claims = getClaimsForToken(token);
        // apply the passed in function to the claims:
        return claimsResolver.apply(claims);
    }

    // get the subject/username from our token by passing in our token
    // as well as getSubject Function to the getClaimForToken method
    public String getUsernameFromToken(String token) {
        return getClaimForToken(token, Claims::getSubject);
    }

    public Date getExpirationTimeFromToken(String token) {
        return getClaimForToken(token, Claims::getExpiration);
    }

    public Date getIssuedAtTimeFromToken(String token) {
        return getClaimForToken(token, Claims::getIssuedAt);
    }

    private Boolean isTokenExpired(String token){
        Date expiration = getExpirationTimeFromToken(token);
        // return if this expiration is before the current time:
        return expiration.before(new Date());
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        // make sure usernames match and make sure token isn't expired:
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

}
