package com.revature.springsecuritydemo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// tells Spring that this is a class that acts as the configuration for security
@EnableWebSecurity
// we can produce beans from this class
@Configuration
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Autowired
    private AuthenticationConfiguration authConfiguration;

    @Autowired
    private EncoderConfig encoderConfig;

    @Autowired
    private UserDetailsService userDetailsService;

    // this method will return an object that will be registered as a bean:
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // set up and connect all of the moving parts we've worked with
        // exception handling, session creation
        // set up rules for WHICH requests need to be authenticated
        http.csrf().disable()
                // we want anyone to be able to authenticate themselves, otherwise no one would be able to use our app
                .authorizeHttpRequests().requestMatchers("/authenticate").permitAll()
                // otherwise, any other request should be authenticated:
                .anyRequest().authenticated().and()
                .exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // before we let the request go through, make sure they have a valid JWT:
        // using our request filter that we set up:
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
        // invoke the build method in order to return filterChain
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return authConfiguration.getAuthenticationManager();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        // set up our authentication manager to use the specific service that we set up and the password encoder bycript:
        auth.userDetailsService(userDetailsService).passwordEncoder(encoderConfig.passwordEncoder());
    }


}
