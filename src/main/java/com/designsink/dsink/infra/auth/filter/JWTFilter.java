package com.designsink.dsink.infra.auth.filter;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.designsink.dsink.entity.user.enums.UserRole;
import com.designsink.dsink.exception.CustomException;
import com.designsink.dsink.exception.ErrorCode;
import com.designsink.dsink.infra.auth.dto.CustomUserDetails;
import com.designsink.dsink.infra.auth.dto.UserDetailsDto;
import com.designsink.dsink.util.JWTUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

	private final JWTUtil jwtUtil;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {

		String authorization = request.getHeader("Authorization");

		// 헤더 검증
		if (authorization == null || !authorization.startsWith("Bearer ")) {
			filterChain.doFilter(request, response);
			return;
		}

		String[] parts = authorization.split(" ");
		if (parts.length != 2) {
			// 잘못된 포맷의 토큰인 경우, BAD_REQUEST (400) 에러를 반환
			throw new CustomException(ErrorCode.INVALID_PARAMETER);
		}

		String accessToken = parts[1];

		// 유효기간 검증
		if (jwtUtil.isExpired(accessToken)) {
			filterChain.doFilter(request, response);
			return;
		}

		Integer userId = jwtUtil.getUserId(accessToken);
		String role = jwtUtil.getRole(accessToken);
		UserDetailsDto userDetailsDto = UserDetailsDto.builder()
			.userId(userId)
			.userRole(UserRole.valueOf(role))
			.build();

		CustomUserDetails customUserDetails = new CustomUserDetails(userDetailsDto);
		Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null,
			customUserDetails.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(authToken);

		filterChain.doFilter(request, response);
	}
}
