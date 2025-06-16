package com.designsink.dsink.service.product;

import static java.nio.file.StandardCopyOption.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.designsink.dsink.exception.CustomException;
import com.designsink.dsink.exception.ErrorCode;

import jakarta.annotation.PostConstruct;

@Service
public class FileStorageService {

	/**
	 * 실제 파일 저장 경로 (uploadDir로부터 생성)
	 */
	private final Path rootLocation;

	public FileStorageService(@Value("${file.upload}") String uploadDir) {
		this.rootLocation = Paths
			.get(uploadDir)
			.toAbsolutePath()
			.normalize();
	}

	/**
	 * Bean 초기화 시 업로드 디렉터리 생성
	 */
	@PostConstruct
	public void init() {
		try {
			Files.createDirectories(rootLocation);
		} catch (IOException ex) {
			throw new CustomException(ErrorCode.DIRECTORY_CREATION_ERROR);
		}
	}

	/**
	 * MultipartFile을 지정된 디렉터리에 저장하고 저장된 파일명을 반환
	 */
	public String store(MultipartFile file) {
		String ext = StringUtils.getFilenameExtension(file.getOriginalFilename());
		String filename = UUID.randomUUID() + (ext != null ? "." + ext : "");

		try (var input = file.getInputStream()) {
			Files.copy(input, rootLocation.resolve(filename), REPLACE_EXISTING);
		} catch (IOException ex) {
			throw new CustomException(ErrorCode.FILE_STORAGE_ERROR);
		}

		return filename;
	}

	public void delete(String filename) {
		try {
			Files.deleteIfExists(rootLocation.resolve(filename));
		} catch (IOException ex) {
			throw new CustomException(ErrorCode.FILE_DELETE_ERROR);
		}
	}
}
