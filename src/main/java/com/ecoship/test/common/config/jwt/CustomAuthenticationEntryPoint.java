package com.ecoship.test.common.config.jwt;

import java.io.IOException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint{

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
		
		Object invalidJwt = request.getAttribute("INVALID_JWT");
        Object expiredJwt = request.getAttribute("EXPIRED_JWT");
        
		String exception = (String) request.getAttribute(JwtProperties.HEADER_STRING);
		String errorCode;
		
		if (invalidJwt != null) {

            errorCode = "유효하지 않은 토큰입니다.";
            setResponse(response, errorCode);
            
        } else if (expiredJwt != null) {
            
            errorCode = "토큰이 만료되었습니다.";
            setResponse(response, errorCode);
        }
		
	}
	
	private void setResponse(HttpServletResponse response, String errorCode) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().println(JwtProperties.HEADER_STRING + " : " + errorCode);
    }

}
