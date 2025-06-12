package com.designsink.dsink.infra.auth;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.designsink.dsink.entity.user.User;
import com.designsink.dsink.exception.ErrorCode;
import com.designsink.dsink.repository.user.UserRepository;
import com.designsink.dsink.infra.auth.dto.CustomUserDetails;
import com.designsink.dsink.infra.auth.dto.UserDetailsDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserDetailServiceImpl implements UserDetailsService {

	private final UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		User user = userRepository.findByEmail(email)
			.orElseThrow(() -> new UsernameNotFoundException(ErrorCode.NOT_FOUND_USER.getDefaultMessage()));

		if (user.getIsDeleted()) {
			throw new UsernameNotFoundException(ErrorCode.NOT_FOUND_USER.getDefaultMessage());
		}

		UserDetailsDto userDetailsDto = UserDetailsDto.builder()
			.userId(user.getId())
			.username(user.getUsername())
			.password(user.getPassword())
			.email(user.getEmail())
			.userRole(user.getUserRole())
			.build();

		return new CustomUserDetails(userDetailsDto);
	}
}
