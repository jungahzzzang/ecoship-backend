package com.ecoship.test.member.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecoship.test.member.entity.Member;
import com.ecoship.test.oauth.util.RefreshToken;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long>{

	Optional<RefreshToken> findByMember(Member member);
}
