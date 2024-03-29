package com.reseausocial.entreprise.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    UserDetailsService userDetailsService;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors();
        http.csrf().disable();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.authorizeRequests().antMatchers("/login").permitAll();
        http.authorizeRequests().antMatchers(HttpMethod.POST,"/api/user").permitAll();
        http.authorizeRequests().antMatchers(HttpMethod.DELETE,"/api/user/**").hasAnyAuthority("ADMIN", "USER");
        http.authorizeRequests().antMatchers( HttpMethod.PUT,"/api/user/**").hasAnyAuthority("ADMIN", "USER");
        http.authorizeRequests().antMatchers( HttpMethod.GET,"/api/user/").hasAnyAuthority("ADMIN");
        http.authorizeRequests().antMatchers( HttpMethod.GET,"/api/user/**").hasAnyAuthority("ADMIN", "USER");
        http.authorizeRequests().antMatchers("/api/article/**").hasAnyAuthority("ADMIN", "USER");
        http.authorizeRequests().antMatchers("/api/comment/**").hasAnyAuthority("ADMIN", "USER");
        http.authorizeRequests().anyRequest().authenticated();
        http.addFilter(new JWTAuthenticationFilter(authenticationManager()));
        http.addFilterBefore(new JWTAuthorisationFilter(), UsernamePasswordAuthenticationFilter.class);

    }

}
