package com.ecoship.test.oauth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class LoginResponseDto {

	private String username;
    private String profileImage;

    
}
