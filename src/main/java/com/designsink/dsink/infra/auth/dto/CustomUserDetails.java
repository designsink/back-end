package com.designsink.dsink.infra.auth.dto;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class CustomUserDetails implements UserDetails {

	private final UserDetailsDto userDetailsDto;

	public CustomUserDetails(UserDetailsDto userDetailsDto) {
		this.userDetailsDto = userDetailsDto;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {

		Collection<GrantedAuthority> authorities = new ArrayList<>();

		authorities.add(new GrantedAuthority() {
			@Override
			public String getAuthority() {
				return "ROLE_" + userDetailsDto.getUserRole().name();
			}
		});

		return authorities;
	}

	@Override
	public String getPassword() {
		return userDetailsDto.getPassword();
	}

	@Override
	public String getUsername() {
		return userDetailsDto.getUsername();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	public Integer getUserId() {
		return userDetailsDto.getUserId();
	}
}
