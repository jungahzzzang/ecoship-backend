package com.ecoship.test.common.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.security.ConditionalOnDefaultWebSecurity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import com.ecoship.test.common.config.jwt.CustomAuthenticationEntryPoint;
import com.ecoship.test.common.config.jwt.JwtProvider;
import com.ecoship.test.common.config.jwt.JwtRequestFilter;
import com.ecoship.test.common.config.jwt.UserDetailsServiceImpl;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@ConditionalOnDefaultWebSecurity
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class SecurityConfig {
	
	public static final String FRONT_URL = "http://localhost:3000";
	
	@Value("${jwt.secretKey}")
	String SECRET_KEY;
	private final JwtProvider jwtProvider;
	private final UserDetailsServiceImpl userDetailsService;
	
	
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
				.addFilterBefore(new JwtRequestFilter(SECRET_KEY, jwtProvider, userDetailsService), UsernamePasswordAuthenticationFilter.class)
				
				.authorizeHttpRequests(request -> request
						.requestMatchers(FRONT_URL+"/**").authenticated().anyRequest().permitAll());
			
			
			return http.build();
	        		
	}

//	@Bean
//	public CorsConfigurationSource corsConfigurationSource() {
//		
//		CorsConfiguration configuration = new CorsConfiguration();
//		configuration.addAllowedOrigin("http://localhost:3000");
//		configuration.addAllowedMethod("*"); // 허용할 Http Method
//		configuration.addAllowedHeader("*");
//		configuration.setAllowCredentials(true); // 내 서버가 응답할 때 json을 js에서 처리할 수 있게 설정
//		configuration.setMaxAge(3600L);
//        configuration.addExposedHeader("AccessToken"); // 헤더에 있는 JWT 토큰을 클라이언트에서 사용할 수 있도록 권한을 주는 부분
//        configuration.addExposedHeader("RefreshToken");
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/api/**", configuration);
//		
//        return source;
//	}
}
