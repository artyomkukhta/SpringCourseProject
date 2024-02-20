package com.example.JavaWEB.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter{
    private UserDetailsService userDetailsService;

    @Autowired
    public WebSecurityConfig(@Qualifier("userDetailsServiceImpl") UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return new MyAccessDeniedHandler();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/start", "/registration", "/js/**", "/css/**").permitAll()
                .antMatchers("/**").permitAll()
                //         .antMatchers("/userId{userId}/**").access("@userSecurity.hasUserId(authentication, #userId)"
                //               + "or hasAuthority('developers:write')")
                //       .antMatchers("/cells/addCell", "/allusers").access("hasAuthority('developers:write')")
                //       .antMatchers("/cells/id{id}/edit", "/cells/id{id}/remove").access("hasAuthority('developers:write')")
                /*.antMatchers(HttpMethod.GET, "/addCell").hasAuthority(Permission.DEVELOPERS_READ.getPermission())//ограниченный доступ /api/**
                .antMatchers(HttpMethod.POST, "/addCell").hasAuthority(Permission.DEVELOPERS_WRITE.getPermission())//ограниченный доступ /api/**
                .antMatchers(HttpMethod.DELETE, "/addCell").hasAuthority(Permission.DEVELOPERS_WRITE.getPermission())//ограниченный доступ /api/**
               */.anyRequest().authenticated()
                .and()
                .exceptionHandling()
                .accessDeniedHandler(accessDeniedHandler())
                .and()
                .formLogin()
                .loginPage("/login")
                .defaultSuccessUrl("/cells", true)
                .permitAll()
                .and()
                .logout()
                .permitAll()
                .and().csrf().disable();

    }

    @Bean
    protected PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    protected DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        daoAuthenticationProvider.setUserDetailsService(userDetailsService);
        return daoAuthenticationProvider;
    }
}

