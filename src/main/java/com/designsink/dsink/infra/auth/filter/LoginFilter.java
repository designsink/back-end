package com.designsink.dsink.infra.auth.filter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Iterator;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.StreamUtils;

import com.designsink.dsink.exception.CustomException;
import com.designsink.dsink.exception.ErrorCode;
import com.designsink.dsink.infra.auth.dto.CustomUserDetails;
import com.designsink.dsink.service.user.dto.request.LoginRequestDto;
import com.designsink.dsink.util.JWTUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class LoginFilter extends UsernamePasswordAuthenticationFilter {

	private static final ObjectMapper objectMapper = new ObjectMapper();
	private final AuthenticationManager authenticationManager;
	private final JWTUtil jwtUtil;

	public LoginFilter(AuthenticationManager authenticationManager, JWTUtil jwtUtil) {
		super(authenticationManager);
		this.authenticationManager = authenticationManager;
		this.jwtUtil = jwtUtil;
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws
		AuthenticationException {

		LoginRequestDto loginRequestDTO = parseLoginRequest(request);

		String username = loginRequestDTO.getEmail();
		String password = loginRequestDTO.getPassword();

		UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password, null);

		return authenticationManager.authenticate(authToken);
	}

	// 로그인 성공시
	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
		Authentication authentication) throws IOException, ServletException {

		CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
		Integer userId = customUserDetails.getUserId();

		Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
		Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
		GrantedAuthority auth = iterator.next();
		String role = auth.getAuthority().replace("ROLE_", "");

		String token = jwtUtil.createToken(userId, role, 30 * 24 * 60 * 60 * 1000L);

		response.addHeader("Authorization", "Bearer " + token);

		// 응답 본문에 JSON 메시지 작성
		response.setContentType("application/json;charset=UTF-8");
		response.setStatus(HttpServletResponse.SC_OK);

		try {
			// JSON 포맷으로 메시지 작성
			String jsonResponse = "{\"message\":\"로그인 성공\"}";
			response.getWriter().write(jsonResponse);
			response.getWriter().flush();
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}

	// 로그인 실패시
	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
		AuthenticationException failed) throws IOException, ServletException {
		if (failed instanceof AuthenticationServiceException) {
			throw new CustomException(ErrorCode.INVALID_PARAMETER);
		}

		throw new CustomException(ErrorCode.AUTH_FAILED_LOGIN);
	}

	// dto로 파싱
	private LoginRequestDto parseLoginRequest(HttpServletRequest request) {
		try {
			ServletInputStream inputStream = request.getInputStream();
			String messageBody = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);

			return objectMapper.readValue(messageBody, LoginRequestDto.class);
		} catch (IOException e) {
			throw new AuthenticationServiceException(ErrorCode.INVALID_PARAMETER.getDefaultMessage());
		}
	}
}
