package com.ecoship.test.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecoship.test.member.entity.User;

/*
 * 기본적인 CRUD 함수를 가지고 있음.
 */
public interface UserRepository extends JpaRepository<User, Long> {
	
	/*
	 * JPA findBy 규칙
	 * select * from user_master where kakao_email = ?
	 */
	public User findByKakaoEmail(String kakaoEmail);
	
	public User findByUserCode(String userCode);

}
