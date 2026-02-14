package com.hyperativa.card_management.api.dto.response;

public record BatchInsertResponse(
        int totalProcessed,
        int inserted,
        int duplicates,
        int failed,
        long processingTimeMs
) {
	
	@Override
	public String toString() {
	    return "totalProcessed: " + totalProcessed +
	           "\ninserted: " + inserted +
	           "\nduplicates: " + duplicates +
	           "\nfailed: " + failed +
	           "\nprocessingTimeMs: " + processingTimeMs;
	}

}

