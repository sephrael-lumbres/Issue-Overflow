// ONLY USE THIS FILE FOR REFERENCE!!!
//
//package com.sephrael.issuetrackingsystem.security;
//
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.security.oauth2.core.user.OAuth2User;
//import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.Collections;
//import java.util.Map;
//
//@SpringBootApplication
//@RestController
//
//@Configuration
//public class SecurityConfig extends WebSecurityConfigurerAdapter {
//
//    // this gets the user's name from OAuth2 Social authentication
//    @GetMapping("/name")
//    public Map<String, Object> name(@AuthenticationPrincipal OAuth2User principal) {
//        return Collections.singletonMap("name", principal.getAttribute("name"));
//    }
//
//    // this gets the user's email address from OAuth2 Social authentication
//    @GetMapping("/email")
//    public Map<String, Object> email(@AuthenticationPrincipal OAuth2User principal) {
//        return Collections.singletonMap("email", principal.getAttribute("email"));
//    }
//
//    public static void main(String[] args) {
//        SpringApplication.run(SecurityConfig.class, args);
//    }
//
//
//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        http.cors().and().csrf().disable();
//        http.authorizeRequests()
//            // the line below resolved the MIME restrictions problem on the LOGIN page
//            .antMatchers("/error", "/js/**", "/assets/**", "/css/**", "/webjars/**").permitAll()
//            .antMatchers("/register.html", "/reset-password.html", "/auth-redirect.html").permitAll()
//            .anyRequest()
//            .authenticated()
//            .and()
//            .logout(l -> l
//                    .logoutSuccessUrl("/login.html").permitAll()
//            )
////            .csrf(c -> c
////                    .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
////            )
//            .oauth2Login()
//                .defaultSuccessUrl("/index.html")
////                .failureUrl("/register.html")
//            .loginPage("/login.html");
//    }
//
//    // Generates a 401 error for unauthenticated users (REFER TO: https://spring.io/guides/tutorials/spring-boot-oauth2/
//
//}