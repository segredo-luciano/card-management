package com.hyperativa.card_management.application.usecase;

import org.springframework.web.multipart.MultipartFile;

import com.hyperativa.card_management.api.dto.response.BatchInsertResponse;

public interface BatchFileProcessmentUseCase {

	BatchInsertResponse execute(MultipartFile file);
}
