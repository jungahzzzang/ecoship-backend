package com.ecoship.test.member.entity;

import com.ecoship.test.authentication.enums.AuthProvider;
import com.ecoship.test.authentication.enums.Role;
import com.ecoship.test.authentication.oauth2.OAuth2UserInfo;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Member extends BaseDateEntity{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String email;
	
	private String name;
	
	private String oauth2Id;
	
	@Enumerated(EnumType.STRING)
	private AuthProvider authProvider;
	
	 @Enumerated(EnumType.STRING)
	 private Role role;
	
	public Member update(OAuth2UserInfo oAuth2UserInfo) {
		this.name = oAuth2UserInfo.getName();
		this.oauth2Id = oAuth2UserInfo.getOAuth2Id();
		
		return this;
	}
}
