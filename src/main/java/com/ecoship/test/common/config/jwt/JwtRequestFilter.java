package com.ecoship.test.common.config.jwt;

import java.io.IOException;
import java.security.Key;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.ecoship.test.common.ResponseDto;
import com.ecoship.test.member.repository.MemberRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {
	
	@Value("${jwt.secretKey}")
	private final String SECRET_KEY;
	private final JwtProvider jwtProvider;
	private final UserDetailsServiceImpl userDetailsService;
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		
		byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
		Key key = Keys.hmacShaKeyFor(keyBytes);
		
		String jwt = resolveToken(request);
		
		if (StringUtils.hasText(jwt) && jwtProvider.validateToken(jwt)) {
			
			Claims claims;
			try {
				claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(jwt).getBody();
			} catch (ExpiredJwtException e) {
				claims = e.getClaims();
			}
			
			if (claims.getExpiration().toInstant().toEpochMilli() < Instant.now().toEpochMilli()) {
				response.setContentType("application/json;charset=UTF-8");
				response.getWriter().println(
						new ObjectMapper().writeValueAsString(ResponseDto.fail("BAD_REQUEST", "Token이 유효하지 않습니다."))
				);
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			}
			
			String subject = claims.getSubject();
            Collection<? extends GrantedAuthority> authorities =
                    Arrays.stream(claims.get(JwtProperties.AUTHORITIES_KEY).toString().split(","))
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList());

            UserDetails principal = userDetailsService.loadUserByUsername(subject);

            Authentication authentication = new UsernamePasswordAuthenticationToken(principal, jwt, authorities);
            SecurityContextHolder.getContext().setAuthentication(authentication);
		}
		
/*		try {
			userCode = JWT.require(Algorithm.HMAC512(JwtProperties.SECRET)).build().verify(token)
					.getClaim("id").asLong();
		} catch (TokenExpiredException e) {
            e.printStackTrace();
            request.setAttribute(JwtProperties.HEADER_STRING, "토큰이 만료되었습니다.");
        } catch (JWTVerificationException e) {
            e.printStackTrace();
            request.setAttribute(JwtProperties.HEADER_STRING, "유효하지 않은 토큰입니다.");
        }*/
		
		filterChain.doFilter(request, response);
		
	}

	private String resolveToken(HttpServletRequest request) {
		
		String bearerToken = request.getHeader(JwtProperties.HEADER_STRING);
		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(JwtProperties.TOKEN_PREFIX)) {
			return bearerToken.substring(7);
		}
		
		return null;
	}
}
