package com.ecoship.test.authentication.oauth2;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import com.ecoship.test.authentication.dto.UserResponseDto;
import com.ecoship.test.authentication.dto.UserResponseDto.TokenInfo;
import com.ecoship.test.authentication.lib.CookieUtils;
import com.ecoship.test.authentication.repository.CookieAuthorizationRequestRepository;
import com.ecoship.test.config.jwt.JwtTokenProvider;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static com.ecoship.test.authentication.repository.CookieAuthorizationRequestRepository.REDIRECT_URI_PARAM_COOKIE_NAME;

import java.io.IOException;
import java.net.URI;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler{

	@Value("${oauth.authorizedRedirectUri}")
	private String redirectUri;
	private final JwtTokenProvider jwtTokenProvider;
	private final CookieAuthorizationRequestRepository cookieAuthorizationRequestRepository;
	
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
		
		String targetUrl = determineTargetUrl(request, response, authentication);
		
		if (response.isCommitted()) {
			log.debug("Response has already been committed.");
			return;
		}
		clearAuthenticationAttributes(request, response);
		getRedirectStrategy().sendRedirect(request, response, targetUrl);
	}
	
	protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
		
		Optional<String> redirectUri = CookieUtils.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME)
				.map(Cookie::getValue);
		
		if (redirectUri.isPresent() && !isAuthorizedRedirectUri(redirectUri.get())) {
			throw new RuntimeException("redirect URIs are not matched.");
		}
		
		String targetUrl = redirectUri.orElse(getDefaultTargetUrl());
		
		//JWT 생성
		UserResponseDto.TokenInfo tokenInfo = jwtTokenProvider.generateToken(authentication);
		
		return UriComponentsBuilder.fromUriString(targetUrl)
				.queryParam("token", tokenInfo.getAccessToken())
				.build().toUriString();
	}

	protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
		super.clearAuthenticationAttributes(request);
		cookieAuthorizationRequestRepository.removeAuthorizationRequestCookie(request, response);
		
	}
	
	private boolean isAuthorizedRedirectUri(String uri) {
        URI clientRedirectUri = URI.create(uri);
        URI authorizedUri = URI.create(redirectUri);

        if (authorizedUri.getHost().equalsIgnoreCase(clientRedirectUri.getHost())
                && authorizedUri.getPort() == clientRedirectUri.getPort()) {
            return true;
        }
        return false;
    }
}
