package com.designsink.dsink.controller.user;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.designsink.dsink.service.user.UserService;
import com.designsink.dsink.service.user.dto.request.RegisterRequestDto;

import lombok.RequiredArgsConstructor;

@RequestMapping("/users")
@RestController
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	@PostMapping("/register")
	public ResponseEntity<?> register(@RequestBody RegisterRequestDto registerRequestDto) {
		userService.register(registerRequestDto);
		return ResponseEntity.ok().body("회원가입 성공");
	}
}
