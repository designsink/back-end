package com.designsink.dsink.service.user;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.designsink.dsink.entity.user.enums.UserRole;
import com.designsink.dsink.exception.CustomException;
import com.designsink.dsink.exception.ErrorCode;
import com.designsink.dsink.entity.user.User;
import com.designsink.dsink.repository.user.UserRepository;
import com.designsink.dsink.service.user.dto.request.RegisterRequestDto;

import lombok.RequiredArgsConstructor;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;

	@Transactional
	public void register(RegisterRequestDto requestDto) {

		// 가입 여부 검사
		if (userRepository.existsByEmail(requestDto.getEmail())) {
			throw new CustomException(ErrorCode.USER_ALREADY_EXISTS);
		}

		User newUser = User.builder()
			.username(requestDto.getUsername())
			.password(bCryptPasswordEncoder.encode(requestDto.getPassword()))
			.email(requestDto.getEmail())
			.userRole(UserRole.ADMIN)
			.build();

		userRepository.save(newUser);
	}

}
