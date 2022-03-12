package com.sephrael.issueoverflow.configuration;

import com.sephrael.issueoverflow.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private DataSource dataSource;

    @Bean
    public UserDetailsService userDetailsService() {
        return new CustomUserDetailsService();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                // the line below is the paths that require authentication and role permissions to access
                .antMatchers("/register", "/login", "/process_register", "/").permitAll()
                .antMatchers("/getting-started", "/getting-started/*", "/knowledge-base/**", "/contact-us").permitAll()
                .antMatchers("/reset-password", "/change-password*").permitAll()
                .antMatchers("/error", "/js/**", "/assets/**", "/css/**", "/webjars/**").permitAll()

                // Update operations on Users allowed to Project Managers
                .antMatchers("/users/edit/*", "/users/update/*").hasAnyAuthority("Project Manager")

                // CRUD operations on Projects allowed to Project Managers
                .antMatchers("/projects/new", "/projects/save", "/projects/delete/*")
                .hasAnyAuthority("Project Manager")

                // CRUD operations on Issues allowed to Project Managers, Admins, and Developers
                .antMatchers("/issues/*/new", "/issues/*/edit/*", "/issues/*/delete/**", "/issues/new", "/issues/update")
                .hasAnyAuthority("Project Manager", "Admin", "Developer")

                // view Issues allowed to all Roles
                .antMatchers("/issues/*/view/*").hasAnyAuthority("Project Manager", "Admin", "Developer", "Guest")

                // deletion of Files only allowed to PMs, Admins, and Developers
                .antMatchers("/files/*/*/delete/*").hasAnyAuthority("Project Manager", "Admin", "Developer")

                // CRUD operations on Comments allowed to all Roles
                .antMatchers("/issues/*/view/*/comment/**").hasAnyAuthority("Project Manager", "Admin", "Developer", "Guest")
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login")
                .usernameParameter("email")
                .defaultSuccessUrl("/dashboard")
                .permitAll()
                .and()
                .logout().logoutSuccessUrl("/login").permitAll();
    }
}