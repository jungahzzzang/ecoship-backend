package com.ecoship.test.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.security.ConditionalOnDefaultWebSecurity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.ecoship.test.common.FilterSkipMatcher;
import com.ecoship.test.jwt.HeaderTokenExtractor;
import com.ecoship.test.jwt.filter.JwtAuthenticationFilter;
import com.ecoship.test.jwt.filter.JwtAuthorizationFilter;
import com.ecoship.test.jwt.handler.AuthenticationFailureHandler;
import com.ecoship.test.jwt.handler.AuthenticationSuccessHandler;
import com.ecoship.test.jwt.handler.AuthorizationFailureHandler;
import com.ecoship.test.jwt.provider.JwtAuthenticationProvider;
import com.ecoship.test.jwt.provider.JwtAuthorizationProvider;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@ConditionalOnDefaultWebSecurity
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class SecurityConfig {
	
	public static final String FRONT_URL = "http://localhost:3000";
	
	private final HeaderTokenExtractor headerTokenExtractor;
	private final AuthenticationConfiguration authenticationConfiguration;
	private final JwtAuthorizationProvider jwtAuthorizationProvider;
	private final AuthenticationSuccessHandler authenticationSuccessHandler;
	private final AuthenticationFailureHandler authenticationFailureHandler;
	private final AuthorizationFailureHandler authorizationFailureHandler;
	
	@Bean
	public BCryptPasswordEncoder encodePassword() {
		
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationManagerBuilder auth) throws Exception {
		
			auth
				.authenticationProvider(jwtAuthorizationProvider)
				.authenticationProvider(jwtAuthenticationProvider());
		
			http
				.cors()
				.and()
				.csrf().disable()
				.sessionManagement()	//session을 사용하지 않음
				.sessionCreationPolicy(SessionCreationPolicy.NEVER)
				.and()
				.exceptionHandling()
				.and()
				.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
				.addFilterBefore(jwtAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class)
				
				.authorizeHttpRequests(request -> request
						.requestMatchers(FRONT_URL+"/**").authenticated().anyRequest().permitAll());
			
			
			return http.build();
	        		
	}
	
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
		
		return authenticationConfiguration.getAuthenticationManager();
	}
	
	@Bean
	public JwtAuthenticationFilter jwtAuthenticationFilter() throws Exception {
		
		JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(authenticationManager(authenticationConfiguration));
		
		//jwtAuthenticationFilter.setFilterProcessesUrl("");
		jwtAuthenticationFilter.setAuthenticationManager(authenticationManager(authenticationConfiguration));
		jwtAuthenticationFilter.setAuthenticationFailureHandler(authorizationFailureHandler);
		jwtAuthenticationFilter.setAuthenticationSuccessHandler(authenticationSuccessHandler);
		jwtAuthenticationFilter.afterPropertiesSet();
		
		return jwtAuthenticationFilter;
	}
	
	
	public JwtAuthorizationFilter jwtAuthorizationFilter() throws Exception {
		
		List<String> skipList = new ArrayList<>();
		
		skipList.add("GET,/user/kakao/callback/**");
		skipList.add("GET,/");
		skipList.add("GET,/favicon.ico");
		
		FilterSkipMatcher matcher = new FilterSkipMatcher(skipList, "/**");
		JwtAuthorizationFilter filter = new JwtAuthorizationFilter(headerTokenExtractor, matcher);
		
		filter.setAuthenticationFailureHandler(authorizationFailureHandler);
		filter.setAuthenticationManager(authenticationManager(authenticationConfiguration));
		
		return filter;
	}
	
	 @Bean
     public JwtAuthenticationProvider jwtAuthenticationProvider() {
        return new JwtAuthenticationProvider(encodePassword());
     }
	
	

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.addAllowedOrigin("http://localhost:3000");
		configuration.addAllowedMethod("*"); // 허용할 Http Method
		configuration.addAllowedHeader("*");
		configuration.setAllowCredentials(true); // 내 서버가 응답할 때 json을 js에서 처리할 수 있게 설정
		configuration.setMaxAge(3600L);
        configuration.addExposedHeader("AccessToken"); // 헤더에 있는 JWT 토큰을 클라이언트에서 사용할 수 있도록 권한을 주는 부분
        configuration.addExposedHeader("RefreshToken");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);
		
        return source;
	}
}
