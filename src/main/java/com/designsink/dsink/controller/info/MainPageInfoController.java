package com.designsink.dsink.controller.info;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.designsink.dsink.service.info.MainPageInfoService;
import com.designsink.dsink.service.info.dto.common.MainPageInfoDto;

import lombok.RequiredArgsConstructor;

@RequestMapping("/main-page")
@RestController
@RequiredArgsConstructor
public class MainPageInfoController {

	private final MainPageInfoService mainPageInfoService;

	@PostMapping
	public ResponseEntity<String> create(@RequestBody MainPageInfoDto requestDto) {
		mainPageInfoService.create(requestDto);
		return ResponseEntity.ok().body("메인페이지 정보 생성 성공");
	}

	@PutMapping("/{infoId}")
	public ResponseEntity<String> update(@PathVariable Integer infoId, @RequestBody MainPageInfoDto requestDto) {
		mainPageInfoService.update(infoId, requestDto);
		return ResponseEntity.ok().body("메인페이지 정보 수정 성공");
	}

	@GetMapping("/{infoId}")
	public ResponseEntity<MainPageInfoDto> find(@PathVariable Integer infoId) {
		return ResponseEntity.ok().body(mainPageInfoService.find(infoId));
	}
}
