package com.ecoship.test.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class WebConfig implements WebMvcConfigurer{

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		
		registry
				.addMapping("/**")
				//.allowedOrigins("/*")	//외부에서 들어오는 모든 url을 허용
				.allowedOrigins("http://localhost:3000")
				.allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")	//허용되는 Method
				.allowedHeaders("*")	//허용되는 Header
				.allowCredentials(true)	//자격증명 허용
				.maxAge(3600);			//허용 시간
	}
}
