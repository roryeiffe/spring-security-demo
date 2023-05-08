package com.revature.springsecuritydemo.controllers;

import com.revature.springsecuritydemo.config.JwtTokenUtil;
import com.revature.springsecuritydemo.models.JwtTokenRequest;
import com.revature.springsecuritydemo.models.JwtTokenResponse;
import com.revature.springsecuritydemo.services.JwtUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

// This class will let us authenticate ourselves by sending a request:
@RestController
// We need this in order to ensure that we can access this endpoint
@CrossOrigin
public class JwtAuthenticationController {

    // we need to have this bean set up in security config for this to work
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;



    // we pass in a username/password and should get a JwtToken in response if the username/password are valid:
    @PostMapping("/authenticate")
    public ResponseEntity<JwtTokenResponse> createAuthenticationToken(@RequestBody JwtTokenRequest authenticationRequest) throws Exception{
        authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());

        // load user details based on the username:
        UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(authenticationRequest.getUsername());

        // Given some user details, we want to generate a token and return it to the user:
        String token = jwtTokenUtil.generateToken(userDetails);

        // if everything went well at this point, we just return the token to the user:
        JwtTokenResponse jwtTokenResponse = new JwtTokenResponse();
        jwtTokenResponse.setToken(token);
        return ResponseEntity.ok(jwtTokenResponse);
    }

    // TODO: come back to this
    // Attempts to authenticate based on username/password and throws the correct exception accordingly
    private void authenticate(String username, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch(DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("INVALID_CREDENTIALS", e);
        }
    }
}
