package com.ecoship.test.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ecoship.test.member.entity.Member;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>{
	
	Optional<Member> findByKakaoEmail(String kakaoEmail);
	
	Member findByUserCode(Long userCode);

}
