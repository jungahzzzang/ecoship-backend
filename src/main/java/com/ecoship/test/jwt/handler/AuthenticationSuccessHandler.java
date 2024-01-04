package com.ecoship.test.jwt.handler;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.ecoship.test.common.ResponseDto;
import com.ecoship.test.jwt.JwtProperties;
import com.ecoship.test.jwt.JwtTokenUtils;
import com.ecoship.test.jwt.UserDetailsImpl;
import com.ecoship.test.oauth.dto.LoginResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler{

	private ObjectMapper mapper = new ObjectMapper();
	
	@Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {

        final UserDetailsImpl userDetails = ( (UserDetailsImpl) authentication.getPrincipal());
        final String token = JwtTokenUtils.createJwtToken(userDetails);

        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");

        // loginInfoDto 객체 생성
        LoginResponseDto loginDto = LoginResponseDto.builder()
                .username(userDetails.getUsername())
                .build();

        // json 형태로 바꾸기
        String result = mapper.writeValueAsString(ResponseDto.success(loginDto));
        response.getWriter().write(result);
        response.addHeader(JwtProperties.HEADER_STRING, JwtProperties.TOKEN_TYPE+" "+token);
    }
}
