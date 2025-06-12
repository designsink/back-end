package com.designsink.dsink.entity.user;

import org.hibernate.annotations.SQLRestriction;

import com.designsink.dsink.entity.common.TimeStampEntity;
import com.designsink.dsink.entity.user.enums.UserRole;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "user")
@Entity(name = "user")
@SQLRestriction("is_deleted = false")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
public class User extends TimeStampEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private String username;

	private String password;

	@Column(unique = true)
	private String email;

	@Enumerated(EnumType.STRING)
	private UserRole userRole;

	@Builder.Default
	private Boolean isDeleted = false;
}
