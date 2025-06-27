package com.designsink.dsink.service.product;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import net.coobird.thumbnailator.Thumbnails;

import com.designsink.dsink.exception.CustomException;
import com.designsink.dsink.exception.ErrorCode;
import com.designsink.dsink.service.product.record.ImagePath;

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
	public ImagePath store(MultipartFile file) {
		String ext = StringUtils.getFilenameExtension(file.getOriginalFilename());
		String originalFilename  = UUID.randomUUID() + (ext != null ? "." + ext : "");
		String thumbnailFilename = UUID.randomUUID() + ".webp";

		try {
			// 1) 원본 저장
			try (InputStream in = file.getInputStream()) {
				Files.createDirectories(rootLocation.resolve("original"));
				Files.copy(in, rootLocation.resolve("original").resolve(originalFilename),
					StandardCopyOption.REPLACE_EXISTING);
			}

			// 2) 썸네일 생성 및 저장 (size: 200×200)
			File originalFile = rootLocation.resolve("original").resolve(originalFilename).toFile();
			Files.createDirectories(rootLocation.resolve("thumbnail"));
			Thumbnails.of(originalFile)
				.size(400, 225)
				.outputFormat("webp")
				.toFile(rootLocation.resolve("thumbnail").resolve(thumbnailFilename).toFile());

		} catch (IOException ex) {
			throw new CustomException(ErrorCode.FILE_STORAGE_ERROR);
		}

		return new ImagePath(
			"original/"  + originalFilename,
			"thumbnail/" + thumbnailFilename
		);
	}

	// 논리적 삭제로 변경
	public void delete(String filename) {
		try {
			Files.deleteIfExists(rootLocation.resolve(filename));
		} catch (IOException ex) {
			throw new CustomException(ErrorCode.FILE_DELETE_ERROR);
		}
	}
}
