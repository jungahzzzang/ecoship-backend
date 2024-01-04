package com.ecoship.test.jwt;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

public class JwtTokenUtils {

	private static final int SEC = 1;
    private static final int MINUTE = 60 * SEC;
    private static final int HOUR = 60 * MINUTE;
    private static final int DAY = 24 * HOUR;

    private static final int JWT_TOKEN_VALID_SEC = 3 * DAY;
    private static final int JWT_TOKEN_VALID_MILLI_SEC = JWT_TOKEN_VALID_SEC * 1000;
	
	public static String CLAIM_EXPIRED_DATE = "EXPIRED_DATE";
	
	public static String ISSUER = "TESTISSUER";
	
	public static String CLAIM_USER_NAME = "USER_NAME";
	
	public static String JWT_SECRET = "secretKeysecretKeysecretKeysecretKeysecretKeysecretKeysecretKeysecretKeysecretKeysecretKey";
	
	public static String createJwtToken(UserDetailsImpl userDetails) {
        String token = null;
        try {
            token = JWT.create()
                    .withIssuer("issuer")
                    .withClaim(CLAIM_USER_NAME, userDetails.getUsername())
                     // 토큰 만료 일시 = 현재 시간 + 토큰 유효기간)
                    .withClaim(CLAIM_EXPIRED_DATE, new Date(System.currentTimeMillis() + JWT_TOKEN_VALID_MILLI_SEC))
                    .sign(generateAlgorithm(JWT_SECRET));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return token;
    }
	
	private static Algorithm generateAlgorithm(String secretKey) {
		
		if (StringUtils.isEmpty(secretKey)) {
			throw new NullPointerException("secretKey is Null");
		}
        return Algorithm.HMAC256(secretKey);
    }
}
