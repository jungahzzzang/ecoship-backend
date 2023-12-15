package com.ecoship.test.oauth.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class LoginResponseDto {

	private String jwtToken;
	private String currentTime;
	private String registerCheck;
	private String nickname;
	
	private Long account_id;
    private String access_token;
    private String refresh_token;
    private Boolean isSignUp;

    @Builder
    public LoginResponseDto(final Long account_id, final String access_token, final String refresh_token, final Boolean isSignUp) {
        this.account_id = account_id;
        this.access_token = access_token;
        this.refresh_token = refresh_token;
        this.isSignUp = isSignUp;
    }
}
