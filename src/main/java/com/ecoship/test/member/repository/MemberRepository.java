package com.ecoship.test.member.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecoship.test.member.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long>{
	
	Optional<Member> findByEmail(String email);

}