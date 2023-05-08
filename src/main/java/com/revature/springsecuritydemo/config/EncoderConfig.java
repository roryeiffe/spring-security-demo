package com.revature.springsecuritydemo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
/*
Let's say we pass in the password "ashes123"
The application will "hash" this and create a randomized string such as dBRE%J%#QYREA#BR4hh43h6
It is impossible to get ashes123 from this string so even if a hacker was able to see "dBRE%J%#QYREA#BR4hh43h6" in the database, hashing irreversible so they couldn't get that back
When the user is logging in, they would put in "ashes123", the programer would hash this value and just make sure it matches the hashed value in the database
 */


@Configuration
public class EncoderConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        // BCrypt is the specific password encoder that we're using:
        return new BCryptPasswordEncoder();
    }
}
