package com.ecoship.test.authentication.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter
public class LoginRequest {

	@NotBlank
	@Email
	private String email;
	
	@NotBlank
	private String password;
	
	@Builder
	public LoginRequest(String email, String password) {
		this.email=email;
		this.password=password;
	}
}
