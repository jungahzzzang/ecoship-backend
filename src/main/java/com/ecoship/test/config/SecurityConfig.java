package com.ecoship.test.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.ecoship.test.authentication.oauth2.OAuth2AuthenticationFailureHandler;
import com.ecoship.test.authentication.oauth2.OAuth2AuthenticationSuccessHandler;
import com.ecoship.test.authentication.repository.CookieAuthorizationRequestRepository;
import com.ecoship.test.authentication.service.CustomOAuthUserService;
import com.ecoship.test.config.jwt.JwtAuthenticationFilter;
import com.ecoship.test.config.jwt.JwtTokenProvider;

import jakarta.servlet.DispatcherType;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
	
	private final CookieAuthorizationRequestRepository cookieAuthorizationRequestRepository;
	private final JwtTokenProvider jwtTokenProvider;
	private final CustomOAuthUserService customOAuthUserService;
	private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
	private final OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;
	
	private static final String[] AUTH_WHITELIST = {
            "/**", "/users/**"
    };
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		
			http
				.cors()
				.and()
				.httpBasic().disable()
				.csrf().disable()
				.formLogin().disable()
				.rememberMe().disable()
				.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
			//요청에 대한 권한 설정
	        http.authorizeRequests()
	                .requestMatchers("/oauth2/**").permitAll()
	                .anyRequest().authenticated();
	        //oauth2 Login
	        http.oauth2Login()
	        		.authorizationEndpoint().baseUri("/oauth2/authorize")	//소셜로그인 url
	        		.authorizationRequestRepository(cookieAuthorizationRequestRepository)	//인증 요청을 cookie에 저장
	        		.and()
	        		.redirectionEndpoint().baseUri("/oauth2/callback/*")	//소셜 인증 후 redirect url
	        		.and()
	        		//UserService는 OAuth2 인증 과정에서 Authentication 생성에 필요한 OAuth2User를 반환하는 클래스를 지정한다.
	        		.userInfoEndpoint().userService(customOAuthUserService)	//회원 정보 처리
	        		.and()
	        		.successHandler(oAuth2AuthenticationSuccessHandler)
	        		.failureHandler(oAuth2AuthenticationFailureHandler);
	        
	        http.logout()
	        		.clearAuthentication(true)
	        		.deleteCookies("JSESSIONID");
	        
	        //jwt filter 설정
	        http.addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);
	        
	        return http.build();
	        		
	}
}
