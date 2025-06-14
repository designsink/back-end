package com.designsink.dsink.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum ErrorCode {
	INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "G-001", "잘못된 파라미터가 전달되었습니다."),
	INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "G-002", "서버 에러가 발생했습니다."),

	// AUTH 관련 에러
	EXPIRED_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH_001", "액세스토큰이 만료되었거나 유효하지 않습니다."),
	NOT_AUTHENTICATED_USER(HttpStatus.UNAUTHORIZED, "AUTH_002", "인증 되지 않은 사용자입니다."),
	FORBIDDEN_USER(HttpStatus.FORBIDDEN, "AUTH_003", "해당 요청의 권한이 없습니다."),
	AUTH_FAILED_LOGIN(HttpStatus.UNAUTHORIZED, "AUTH_004", "아이디, 비밀번호가 일치하지 않습니다."),

	// 파일 관련 에러
	FILE_EMPTY(HttpStatus.BAD_REQUEST, "FILE_001", "파일이 비어있습니다."),
	FILE_TOO_LARGE(HttpStatus.BAD_REQUEST, "FILE_002", "파일 크기가 너무 큽니다. 최대 허용 크기는 5MB 바이트 입니다."),
	FILE_STORAGE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "FILE_003", "파일 저장 중 오류가 발생했습니다."),
	DIRECTORY_CREATION_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "FILE_004", "업로드 디렉터리 생성 중 오류가 발생했습니다."),
	FILE_CATEGORY_ERROR(HttpStatus.BAD_REQUEST, "FILE_005", "올바르지 않은 카테고리입니다."),

	// 회원 관련 에러
	USER_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "USER_001", "이미 존재하는 회원입니다."),
	NOT_FOUND_USER(HttpStatus.NOT_FOUND, "USER_002", "이메일 또는 비밀번호가 잘못됐습니다."),

	// 메인 페이지 관련 에러
	NOT_FOUND_PAGE_INFO(HttpStatus.NOT_FOUND, "MAIN_001", "메인 페이지 정보가 없습니다.");


	// http 상태 코드
	private final HttpStatus status;
	// 커스텀 에러 코드
	private final String code;
	// 기본 에러 메시지
	private final String defaultMessage;

	ErrorCode(HttpStatus status, String code, String defaultMessage) {
		this.status = status;
		this.code = code;
		this.defaultMessage = defaultMessage;
	}
}
