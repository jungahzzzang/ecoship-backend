package com.ecoship.test.config.jwt;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.ecoship.test.authentication.dto.UserResponseDto;
import com.ecoship.test.config.ExpireTime;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

/*
 * JWT 토큰을 만들어주는 유틸 클래스
 */
@Slf4j
@Component
public class JwtTokenProvider {
	
	private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String AUTHORITIES_KEY = "auth";
    private static final String BEARER_TYPE = "Bearer";
    private static final String TYPE_ACCESS = "access";
    private static final String TYPE_REFRESH = "refresh";

    private final Key key;

    public JwtTokenProvider(@Value("${jwt.secretKey}") String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    //Authentication을 가지고 AccessToken, RefreshToken을 생성하는 메서드
    public UserResponseDto.TokenInfo generateToken(Authentication authentication) {
    	return generateToken(authentication.getName(), authentication.getAuthorities());
    }
    
    //name, authorities 를 가지고 AccessToken, RefreshToken 을 생성하는 메서드
    public UserResponseDto.TokenInfo generateToken(String name, Collection<? extends GrantedAuthority> inputAuthorities) {
    	//권한 가져오기
    	String authorities = inputAuthorities.stream()
    			.map(GrantedAuthority::getAuthority)
    			.collect(Collectors.joining(","));
    	
    	Date now = new Date();
    	
    	//Generate AccessToken
        String accessToken = Jwts.builder()
                .setSubject(name)
                .claim(AUTHORITIES_KEY, authorities)
                .claim("type", TYPE_ACCESS)
                .setIssuedAt(now)   //토큰 발행 시간 정보
                .setExpiration(new Date(now.getTime() + ExpireTime.ACCESS_TOKEN_EXPIRE_TIME))  //토큰 만료 시간 설정
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        //Generate RefreshToken
        String refreshToken = Jwts.builder()
                .claim("type", TYPE_REFRESH)
                .setIssuedAt(now)   //토큰 발행 시간 정보
                .setExpiration(new Date(now.getTime() + ExpireTime.REFRESH_TOKEN_EXPIRE_TIME)) //토큰 만료 시간 설정
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    	
    	return UserResponseDto.TokenInfo.builder()
    			.grantType(BEARER_TYPE)
    			.accessToken(accessToken)
    			.accessTokenExpirationTime(ExpireTime.ACCESS_TOKEN_EXPIRE_TIME)
    			.refreshToken(refreshToken)
    			.refreshTokenExpirationTime(ExpireTime.REFRESH_TOKEN_EXPIRE_TIME)
    			.build();
    }
    
    //JWT 토큰을 복호화하여 토큰에 들어있는 정보를 꺼내는 메서드
    public Authentication getAuthentication(String accessToken) {
    	//토큰 복호화
    	Claims claims = parseClaims(accessToken);
    	
    	if(claims.get(AUTHORITIES_KEY) == null) {
    		throw new RuntimeException("권한 정보가 없는 토큰입니다.");
    	}
    	
    	//권한 정보 가져오기
    	Collection<? extends GrantedAuthority> authorities =
    			Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
    					.map(SimpleGrantedAuthority::new)
    					.collect(Collectors.toList());
    	
    	//UserDetails 객체를 만들어서 Authentication 리턴
    	UserDetails principal = new User(claims.getSubject(), "", authorities);
    	return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }
    
    //토큰 정보를 검증하는 메서드
    public boolean validateToken(String token) {
    	try {
    		Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
    		return true;
    	} catch (SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT Token", e);
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT Token", e);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT Token", e);
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty.", e);
        }
    	return false;
    }
    
    public Claims parseClaims(String accessToken) {
    	try {
    		return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
    	}catch (ExpiredJwtException e) {
			return e.getClaims();
		}
    }
    
    public String resolveToken(HttpServletRequest request) {
    	String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
    	if(StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_TYPE)) {
    		return bearerToken.substring(7);
    	}
    	return null;
    }
}
