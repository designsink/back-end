package com.designsink.dsink.util;

import java.util.Date;
import java.util.HexFormat;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;

@Component
public class JWTUtil {

	public static final String USER_ID_CLAIM = "userId";
	public static final String ROLE_CLAIM = "role";

	private final SecretKey secretKey;

	public JWTUtil(@Value("${spring.jwt.secret}") String secretKey) {
		byte[] secretBytes = HexFormat.of().parseHex(secretKey);
		this.secretKey = new SecretKeySpec(secretBytes, "HmacSHA256");
	}

	public Integer getUserId(String token) {
		return Jwts.parser()
			.verifyWith(secretKey)
			.build()
			.parseSignedClaims(token)
			.getPayload()
			.get(USER_ID_CLAIM, Integer.class);
	}

	public String getRole(String token) {
		return Jwts.parser()
			.verifyWith(secretKey)
			.build()
			.parseSignedClaims(token)
			.getPayload()
			.get(ROLE_CLAIM, String.class);
	}

	public Boolean isExpired(String token) {
		return Jwts.parser()
			.verifyWith(secretKey)
			.build()
			.parseSignedClaims(token)
			.getPayload()
			.getExpiration()
			.before(new Date());
	}

	public String createToken(Integer userId, String role, Long expiredMs) {
		return Jwts.builder()
			.claim(USER_ID_CLAIM, userId)
			.claim(ROLE_CLAIM, role)
			.issuedAt(new Date(System.currentTimeMillis()))
			.expiration(new Date(System.currentTimeMillis() + expiredMs))
			.signWith(secretKey)
			.compact();
	}

}
