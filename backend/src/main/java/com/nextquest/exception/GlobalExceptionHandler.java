package com.nextquest.exception;

import jakarta.validation.ConstraintViolationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.converter.HttpMessageNotReadableException;

@RestControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException exception) {
		ErrorResponse response = new ErrorResponse(
				LocalDateTime.now(),
				HttpStatus.NOT_FOUND.value(),
				"Not Found",
				exception.getMessage(),
				null);

		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
	}

	@ExceptionHandler(DuplicateResourceException.class)
	public ResponseEntity<ErrorResponse> handleDuplicateResource(DuplicateResourceException exception) {
		ErrorResponse response = new ErrorResponse(
				LocalDateTime.now(),
				HttpStatus.CONFLICT.value(),
				"Conflict",
				exception.getMessage(),
				null);

		return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException exception) {
		Map<String, String> fields = new HashMap<>();

		exception.getBindingResult().getFieldErrors().forEach(error -> {
			fields.put(error.getField(), error.getDefaultMessage());
		});

		ErrorResponse response = new ErrorResponse(
				LocalDateTime.now(),
				HttpStatus.BAD_REQUEST.value(),
				"Bad Request",
				"Validation failed",
				fields);

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException exception) {
		Map<String, String> fields = new HashMap<>();

		exception.getConstraintViolations().forEach(violation -> {
			String field = violation.getPropertyPath().toString();

			fields.putIfAbsent(field, violation.getMessage());
		});

		ErrorResponse response = new ErrorResponse(
				LocalDateTime.now(),
				HttpStatus.BAD_REQUEST.value(),
				"Bad Request",
				"Constraint violation",
				fields);

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ErrorResponse> handleUnreadableRequest(HttpMessageNotReadableException exception) {
		ErrorResponse response = new ErrorResponse(
				LocalDateTime.now(),
				HttpStatus.BAD_REQUEST.value(),
				"Bad Request",
				"Invalid request body. Check JSON syntax, field types " + "and allowed status values.",
				null);

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	}

	private boolean isDuplicateLibraryEntry(Throwable exception) {
		Throwable current = exception;

		while (current != null) {

			if (current instanceof org.hibernate.exception.ConstraintViolationException violation) {

				if ("uk_library_entries_user_game".equals(violation.getConstraintName())) {
					return true;
				}
			}
			current = current.getCause();
		}
		return false;
	}

	@ExceptionHandler (DataIntegrityViolationException.class)
	public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(DataIntegrityViolationException exception) {
		if (isDuplicateLibraryEntry(exception)) {

			ErrorResponse response = new ErrorResponse(
					LocalDateTime.now(),
					HttpStatus.CONFLICT.value(),
					"Conflict",
					"Game already exists in this user's library",
					null);

			return ResponseEntity
					.status(HttpStatus.CONFLICT)
					.body(response);
		}
		
		logger.error("Unexpected database integrity violation", exception);

		ErrorResponse response = new ErrorResponse(
				LocalDateTime.now(),
				HttpStatus.INTERNAL_SERVER_ERROR.value(),
				"Internal Server Error",
				"An unexpected error occurred while saving data.",
				null);

		return ResponseEntity
				.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(response);


	}

}
