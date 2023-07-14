package com.ecoship.test.authentication.enums;

import lombok.Getter;

@Getter
public enum Role {

	ROLE_GUSET("게스트"), ROLE_USER("사용자");
	
	private String description;
	
	private Role(String description) {
		this.description = description;
	}
}
