package com.nextquest.exception;

import java.time.LocalDateTime;
import java.util.Map;

public class ErrorResponse {

	private LocalDateTime timestamp;
	private int status;
	private String error;
	private String message;
	private Map<String, String> fields;

	public ErrorResponse(LocalDateTime timestamp, int status, String error, String message, Map<String, String> fields) {
		this.timestamp = timestamp;
		this.status = status;
		this.error = error;
		this.message = message;
		this.fields = fields;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public int getStatus() {
		return status;
	}

	public String getError() {
		return error;
	}

	public String getMessage() {
		return message;
	}

	public Map<String, String> getFields() {
		return fields;
	}
}
