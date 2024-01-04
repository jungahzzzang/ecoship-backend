package com.ecoship.test.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import com.ecoship.test.jwt.JwtProperties;

@Configuration
public class CorsConfig {

	@Bean
	public CorsFilter corsFilter() {
		
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowCredentials(true);		//서버 응답 시 json을 자바스크립트에서 처리할 수 있음
		config.addAllowedOriginPattern("*");	//모든 ip에 응답 허용
		config.addAllowedHeader("*");           //모든 header 응답 허용
		config.addExposedHeader("*");           
		config.addAllowedMethod("*");           //모든 요청 메소드 응답 허용
		//config.addExposedHeader(JwtProperties.HEADER_STRING);
		source.registerCorsConfiguration("/api/**", config);
		
		return new CorsFilter(source);
	}
}
