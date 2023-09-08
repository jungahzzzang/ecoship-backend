package com.ecoship.test.member.repository;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.ecoship.test.member.entity.Member;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class MemberQuerydslRepository {

	@PersistenceContext
	private EntityManager eManager;
	
//	@Transactional(readOnly = true)
//	public Member findByKakaoId(String kakaoId) {
//		JPAQueryFactory jpaQueryFactory = new JPAQueryFactory(eManager);
//		
//		return jpaQueryFactory
//				.selectFrom(member)
//				.where()
//				.fetchOne();
//	}
}
