package com.ecoship.test.common.config;


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

import com.ecoship.test.common.config.jwt.CustomAuthenticationEntryPoint;
import com.ecoship.test.common.config.jwt.JwtProvider;
import com.ecoship.test.common.config.jwt.JwtRequestFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
	
	public static final String FRONT_URL = "http://localhost:3000";
	
	private final CorsFilter corsFilter;
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		
			http
					.cors()
					.and()
					.csrf().disable()
					.sessionManagement()	//session을 사용하지 않음
					.sessionCreationPolicy(SessionCreationPolicy.NEVER)
					.and()
					.exceptionHandling()
					.authenticationEntryPoint(new CustomAuthenticationEntryPoint())
					.and()
					.addFilterBefore(new JwtRequestFilter(), UsernamePasswordAuthenticationFilter.class)
					
					.authorizeHttpRequests(request -> request
							.requestMatchers(FRONT_URL+"/**").authenticated().anyRequest().permitAll());
					
					
	        return http.build();
	        		
	}
}
