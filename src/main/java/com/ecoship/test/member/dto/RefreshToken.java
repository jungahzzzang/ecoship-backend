package com.ecoship.test.member.dto;

import com.ecoship.test.member.entity.BaseDateEntity;
import com.ecoship.test.member.entity.Member;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken extends BaseDateEntity {
	
	@Id
	private String email;
	
	private String token;
	
	public RefreshToken(Member member, String refreshToken) {
		this.email=member.getKakaoEmail();
		this.token=refreshToken;
	}
	
	public void updateToken(String refreshToken) {
		this.token=refreshToken;
	}

}
