package com.ecoship.test.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecoship.test.member.dto.RefreshToken;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String>{

	void deleteByToken(String refreshToken);
}
