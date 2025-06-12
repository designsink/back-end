package com.designsink.dsink.infra.auth.dto;

import com.designsink.dsink.entity.user.enums.UserRole;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class UserDetailsDto {
	private Integer userId;
	private String username;
	private String password;
	private String email;
	private UserRole userRole;
}
