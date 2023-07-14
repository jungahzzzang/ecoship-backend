package com.ecoship.test.authentication.domain.oauth;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

@Component
public class RequestOauthInfoService {

	private final Map<OAuthProvider, OAuthApiClient> clients;
	
	public RequestOauthInfoService(List<OAuthApiClient> clients) {
		this.clients = clients.stream().collect(
					Collectors.toUnmodifiableMap(OAuthApiClient::oAuthProvider, Function.identity())
				);
	}
	
	public OAuthInfoResponse request(OAuthLoginParams params) {
		OAuthApiClient client = clients.get(params.oAuthProvider());
		String accessToken = client.requestAccessToken(params);
		return client.requestOAuthInfo(accessToken);
	}
}
