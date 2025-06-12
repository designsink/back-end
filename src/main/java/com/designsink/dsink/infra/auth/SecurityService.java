package com.designsink.dsink.infra.auth;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.designsink.dsink.entity.user.User;
import com.designsink.dsink.exception.CustomException;
import com.designsink.dsink.exception.ErrorCode;
import com.designsink.dsink.repository.user.UserRepository;
import com.designsink.dsink.infra.auth.dto.CustomUserDetails;

import lombok.RequiredArgsConstructor;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class SecurityService {

	private final UserRepository userRepository;

	public User getLoggedInUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || !authentication.isAuthenticated()
			|| authentication instanceof AnonymousAuthenticationToken) {
			throw new CustomException(ErrorCode.NOT_AUTHENTICATED_USER);
		}

		CustomUserDetails userDetails = (CustomUserDetails)authentication.getPrincipal();
		Integer userId = userDetails.getUserId();

		return userRepository.findById(userId)
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_AUTHENTICATED_USER));
	}
}
