package com.ecoship.test.common.config.jwt;

import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import com.ecoship.test.member.entity.Member;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtProvider {
	
	private final UserDetailsService userDetailsService;

	@Value("${jwt.secretKey}")
    private String JWT_SECRET;
	private Key key;
	
	private static final int ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 30; // 30분
	//private static final int ACCESS_TOKEN_EXPIRE_TIME = 1000 * 10 * 6 * 5; //5분
	private static final int REFRESH_TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 24 * 14; // 2주일
	
	@PostConstruct
	protected void keyInit() {
		key = Keys.hmacShaKeyFor(JWT_SECRET.getBytes());
	}
	
	public TokenDto createTokenDto(Member member) {
		long now = new Date().getTime();
		
		String accessToken = Jwts.builder()
				.setSubject(member.getKakaoEmail())
				.setIssuedAt(new Date())
				.setExpiration(new Date(now + ACCESS_TOKEN_EXPIRE_TIME))
				.signWith(key, SignatureAlgorithm.HS256)
				.compact();
		
		String refreshToken = Jwts.builder()
				.setSubject(member.getKakaoEmail())
				.setIssuedAt(new Date())
				.setExpiration(new Date(now + REFRESH_TOKEN_EXPIRE_TIME))
				.signWith(key, SignatureAlgorithm.HS256)
				.compact();
		
		return new TokenDto(accessToken, refreshToken);
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
	
	public Authentication getAuthentication(String token) {
        String email = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject();
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }
}