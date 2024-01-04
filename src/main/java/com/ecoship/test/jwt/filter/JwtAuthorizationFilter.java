package com.ecoship.test.jwt.filter;

import java.io.IOException;

import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;

import com.ecoship.test.jwt.HeaderTokenExtractor;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JwtAuthorizationFilter extends AbstractAuthenticationProcessingFilter {

	private final HeaderTokenExtractor extractor;
	
	public JwtAuthorizationFilter(HeaderTokenExtractor extractor, RequestMatcher requiresAuthenticationRequestMatcher){
        super(requiresAuthenticationRequestMatcher);
        this.extractor= extractor;
    }

	@Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {

        log.info("JWT_FILTER : attemptAutentication () 실행 ");

        // JWT 값을 담아주는 변수 
        String tokenPayload = request.getHeader("Authorization");

        if (tokenPayload == null || tokenPayload.equals("")) {
            throw new AuthenticationCredentialsNotFoundException("토큰이 존재하지 않습니다");
        }

        UsernamePasswordAuthenticationToken jwtToken = new UsernamePasswordAuthenticationToken(extractor.extract(tokenPayload),null);

        return super
                .getAuthenticationManager()
                .authenticate(jwtToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        /*
         *  SecurityContext 사용자 Token 저장소를 생성합니다.
         *  SecurityContext 에 사용자의 인증된 Token 값을 저장합니다.
         */
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authResult);
        SecurityContextHolder.setContext(context);

        //FilterChain chain 해당 필터가 실행 후 다른 필터도 실행 할 수 있도록 연결 시켜주는 메서드
        chain.doFilter(request,response);
    }


    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        /*
         *	로그인을 한 상태에서 Token값을 주고받는 상황에서 잘못된 Token값이라면
         *	인증이 성공하지 못한 단계 이기 때문에 잘못된 Token값을 제거합니다.
         *	모든 인증받은 Context 값이 삭제 됩니다.
         */
        SecurityContextHolder.clearContext();
        super.unsuccessfulAuthentication(request,response,failed);
    }
}
