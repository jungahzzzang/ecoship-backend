package com.ecoship.test.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.CorsFilter;

import com.ecoship.test.config.jwt.CustomAuthenticationEntryPoint;
import com.ecoship.test.config.jwt.JwtRequestFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
	
	public static final String FRONT_URL = "http://localhost:3000";
	
	private static final String[] AUTH_WHITELIST = {
            "/**", "/users/**"
    };
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		
			http.csrf().disable()
					.cors()
					.and()
					.httpBasic().disable()
					.formLogin().disable()
					.authorizeHttpRequests(request -> request
							.requestMatchers(FRONT_URL+"/**").authenticated().anyRequest().permitAll())
					.sessionManagement()	//session을 사용하지 않음
					.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
					.and()										
					.exceptionHandling()
					.authenticationEntryPoint(new CustomAuthenticationEntryPoint())
					.and()
					.addFilterBefore(new JwtRequestFilter(), UsernamePasswordAuthenticationFilter.class);
					
					
	        return http.build();
	        		
	}
}
