package com.ecoship.test.common.config.jwt;

import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import com.ecoship.test.member.entity.Member;
import com.ecoship.test.member.repository.RefreshTokenRepository;
import com.ecoship.test.oauth.util.RefreshToken;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtProvider {
	
	private final RefreshTokenRepository refreshTokenRepository;
	private Key key;
	private static final String AUTHORITIES_KEY = "auth";
    private static final String BEARER_PREFIX = "Bearer ";
	//private static final int ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 30; // 30분
	private static final int ACCESS_TOKEN_EXPIRE_TIME = 1000 * 10 * 6 * 5; //5분
	private static final int REFRESH_TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 24 * 14; // 2주일
	
	long now = (new Date().getTime());
	Date accessTokenExpiresIn = new Date(now + ACCESS_TOKEN_EXPIRE_TIME);
	Date refreshTokenExpiresIn = new Date(now + REFRESH_TOKEN_EXPIRE_TIME);
	
	public JwtProvider(@Value("${jwt.secretKey}") String secretKey, RefreshTokenRepository refreshTokenRepository) {
		this.refreshTokenRepository = refreshTokenRepository;
		byte[] keyBytes = Decoders.BASE64.decode(secretKey);
		this.key = Keys.hmacShaKeyFor(keyBytes);
	}
	
	public String createToken(String userId, String email) {
		Claims claims = Jwts.claims().setSubject(userId);
		claims.put("email", email);
		
		return Jwts.builder()
				.setClaims(claims)			//정보 저장
				.setIssuedAt(new Date())	//토큰 발행 시간 정보
				.setExpiration(accessTokenExpiresIn)
				.signWith(key, SignatureAlgorithm.HS256)
				.compact();
	}
	
	public String createRefreshToken(String userId) {
		Claims claims = Jwts.claims().setSubject(userId);
		
		return Jwts.builder()
				.setClaims(claims)			//정보 저장
				.setIssuedAt(new Date())	//토큰 발행 시간 정보
				.setExpiration(refreshTokenExpiresIn)
				.signWith(key, SignatureAlgorithm.HS256)
				.compact();
	}
	
	public boolean validateToken(String token) throws ExpiredJwtException {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (SignatureException ex) {
            log.error("Invalid JWT signature");
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token");
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty.");
        }
        return false;
    }
	
	public Member getMemberAuthentication() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || AnonymousAuthenticationToken.class.isAssignableFrom(authentication.getClass())) {
			return null;
		}
		return ((UserDetailsImpl) authentication.getPrincipal()).getMember();
	}
}
