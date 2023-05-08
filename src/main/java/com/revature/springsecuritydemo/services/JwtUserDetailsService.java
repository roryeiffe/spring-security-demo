package com.revature.springsecuritydemo.services;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

// dummy class for now that "authenticates" or just checks that the username is admin and returns a user object
@Service
public class JwtUserDetailsService implements UserDetailsService {
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // dummy check to see if username is equal to "admin":
        if(username.equals("admin")) {
            return new User("admin", "$2a$12$VU5137Ujeo8nMx6Bb2VZFOjsG/uV6cTJ/Cl1QpcSX0aQkF1D//bBS", new ArrayList<>());
        }
        else {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
    }
    // dummy implementation of returning dummy data:


}
