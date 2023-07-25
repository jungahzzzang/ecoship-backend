package com.ecoship.test.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecoship.test.member.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long>{
	
	Member findByKakaoEmail(String kakaoEmail);
	
	Member findByUserCode(Long userCode);

}
