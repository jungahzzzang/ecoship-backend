package com.ecoship.test.config.auth.naver;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.ecoship.test.authentication.domain.oauth.OAuthApiClient;
import com.ecoship.test.authentication.domain.oauth.OAuthInfoResponse;
import com.ecoship.test.authentication.domain.oauth.OAuthLoginParams;
import com.ecoship.test.authentication.domain.oauth.OAuthProvider;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class NaverApiClient implements OAuthApiClient {
	
	private static final String GRANT_TYPE = "authorization_code";

	@Value("${spring.security.oauth2.client.provider.naver.token-uri")
    private String authUrl;

    @Value("${spring.security.oauth2.client.provider.naver.user-info-uri}")
    private String apiUrl;

    @Value("${spring.security.oauth2.client.registration.naver.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.naver.client-secret}")
    private String clientSecret;

    private final RestTemplate restTemplate;

    @Override
    public OAuthProvider oAuthProvider() {
        return OAuthProvider.NAVER;
    }

	@Override
	public String requestAccessToken(OAuthLoginParams params) {
		String url = authUrl;

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = params.makeBody();
        body.add("grant_type", GRANT_TYPE);
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);

        HttpEntity<?> request = new HttpEntity<>(body, httpHeaders);

        NaverTokens response = restTemplate.postForObject(url, request, NaverTokens.class);

        assert response != null;
        return response.getAccessToken();
	}

	@Override
	public OAuthInfoResponse requestOAuthInfo(String accessToken) {
		String url = apiUrl;

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        httpHeaders.set("Authorization", "Bearer " + accessToken);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();

        HttpEntity<?> request = new HttpEntity<>(body, httpHeaders);

        return restTemplate.postForObject(url, request, NaverInfoResponse.class);
	}

}
