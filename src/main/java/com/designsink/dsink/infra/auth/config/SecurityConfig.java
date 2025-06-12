package com.designsink.dsink.infra.auth.config;

import java.util.Collections;
import java.util.List;

import org.springframework.boot.autoconfigure.http.client.HttpClientProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import com.designsink.dsink.exception.FilterExceptionHandler;
import com.designsink.dsink.infra.auth.filter.JWTFilter;
import com.designsink.dsink.infra.auth.filter.LoginFilter;
import com.designsink.dsink.util.JWTUtil;

import jakarta.servlet.http.HttpServletRequest;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	private final AuthenticationConfiguration authenticationConfiguration;
	private final JWTUtil jwtUtil;

	public SecurityConfig(AuthenticationConfiguration authenticationConfiguration, JWTUtil jwtUtil) {
		this.authenticationConfiguration = authenticationConfiguration;
		this.jwtUtil = jwtUtil;
	}

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws
		Exception {
		return configuration.getAuthenticationManager();
	}

	@Bean
	public LoginFilter loginFilter(AuthenticationManager authenticationManager) {
		LoginFilter loginFilter = new LoginFilter(authenticationManager, jwtUtil);
		loginFilter.setFilterProcessesUrl("/users/login");
		return loginFilter;
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http, LoginFilter loginFilter,
		HttpClientProperties httpClientProperties) throws Exception {

		http
			.cors((cors) -> cors
				.configurationSource(new CorsConfigurationSource() {
					@Override
					public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
						CorsConfiguration configuration = new CorsConfiguration();

						configuration.setAllowedOrigins(
							List.of("http://localhost:3000"));
						configuration.setAllowedMethods(Collections.singletonList("*"));
						configuration.setAllowCredentials(true);
						configuration.setAllowedHeaders(Collections.singletonList("*"));
						configuration.setMaxAge(3600L);

						return configuration;
					}
				}));

		// csrf 비활성
		http
			.csrf(AbstractHttpConfigurer::disable);

		// 폼 로그인 비활성
		http
			.formLogin(AbstractHttpConfigurer::disable);

		// http basic 인증 비활성
		http
			.httpBasic(AbstractHttpConfigurer::disable);

		// 경로별 인가 작업
		http
			.authorizeHttpRequests((auth) -> auth
			.requestMatchers("/users/login", "/users/register").permitAll()
			.requestMatchers(HttpMethod.GET, "/main-page/**").permitAll()
			.requestMatchers("/main-page/**").hasRole("ADMIN")
			.anyRequest().authenticated()
		);

		http
			.addFilterAt(loginFilter, UsernamePasswordAuthenticationFilter.class);

		http
			.addFilterBefore(new FilterExceptionHandler(), LogoutFilter.class);

		http
			.addFilterBefore(new JWTFilter(jwtUtil), LoginFilter.class);

		/** 세션 설정
		 * jwt에서 session을 stateless로 관리하는 이유는?
		 * 로드밸런싱 환경에서 “어떤 서버에 접속되더라도” 인증 정보를 공유할 필요가 없어짐
		 * 여러 서비스가 분산되어 있어도, 각 서비스는 토큰만 검증하면 됨
		 */
		http.sessionManagement((session) -> session
			.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
		);

		return http.build();
	}
}
