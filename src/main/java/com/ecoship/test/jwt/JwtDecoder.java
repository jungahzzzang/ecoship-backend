package com.ecoship.test.jwt;

import java.util.Date;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import static com.ecoship.test.jwt.JwtTokenUtils.*;

@Component
public class JwtDecoder {

	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Value("${jwt.secretKey}")
	public static String SECRET_KEY;
	
	@Value("${jwt.tokenExpiry}")
	public static String CLAIM_EXPIRED_DATE;
	
	@Value("${jwt.issuer}")
	public static String ISSUER;
	
	@Value("${jwt.userName}")
	public static String CLAIM_USER_NAME;
	
	@Autowired
	public JwtDecoder(@Value("${jwt.secret-key}") String SECRET_KEY) {
		
		this.SECRET_KEY = SECRET_KEY;
	}
	
	public UserInfo decodeUserName(String token) {
		DecodedJWT decodedJWT = isValidToken(token).orElseThrow(()->new AuthenticationServiceException("토큰이 유효하지 않습니다"));
        Date expiredDate = decodedJWT.getClaim(CLAIM_EXPIRED_DATE).asDate();


        Date now = new Date();
        if(expiredDate.before(now)){
            throw new AuthenticationServiceException("유효시간이 지난 토큰입니다 ");
        }

        String username = decodedJWT
                .getClaim(CLAIM_USER_NAME)
                .asString();


        return UserInfo.builder()
                .username(username)
                .build();
	}
	
	private Optional<DecodedJWT> isValidToken(String token){

        DecodedJWT jwt = null;
        try{
            Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);
            JWTVerifier verifier = JWT
                                    .require(algorithm)
                                    .build();

            jwt = verifier.verify(token);
        }catch (Exception e){
            log.error(e.getMessage());
        }

        return Optional.ofNullable(jwt);
    }
}
