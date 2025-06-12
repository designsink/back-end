package com.designsink.dsink.service.user.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequestDto {
	private String username;
	private String email;
	private String password;
}
