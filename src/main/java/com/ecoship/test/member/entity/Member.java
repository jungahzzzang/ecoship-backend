package com.ecoship.test.member.entity;

import org.hibernate.annotations.SQLDelete;

import com.ecoship.test.oauth.util.BaseDateEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity 
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@SQLDelete(sql = "UPDATE member SET delete_check = 'Y' where id = ?")
public class Member extends BaseDateEntity{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(unique = true)
    private String email;
	
	@Column(unique = true)
    private Long kakaoId;

    @Column(unique = true)
    private String nickname;

    @Column
    private String name;
    
    @Column
    private String deleteCheck;
}