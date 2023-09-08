package com.ecoship.test.common.config.jwt;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TokenDto {

	private String accessToken;
	private String refreshToken;
}
