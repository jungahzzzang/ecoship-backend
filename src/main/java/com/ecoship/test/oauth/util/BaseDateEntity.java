package com.ecoship.test.oauth.util;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

@MappedSuperclass	//JPA Entity 클래스들이 BaseDateEntity 상속 시 필드들도 컬럼으로 인식하도록 함
@EntityListeners(AuditingEntityListener.class)	//클래스에 Auditing 기능 포함
@Getter
@Setter
public abstract class BaseDateEntity {

	@CreatedDate
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss",timezone = "Asia/Seoul")
	private LocalDateTime createdAt;

    @LastModifiedDate
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss",timezone = "Asia/Seoul")
    private LocalDateTime modifiedAt;
}
