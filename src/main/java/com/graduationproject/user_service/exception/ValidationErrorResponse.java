package com.graduationproject.user_service.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ValidationErrorResponse extends ErrorResponse {
    private Map<String, String> errors;

    public ValidationErrorResponse(int status, String message, LocalDateTime timestamp, String path,
            Map<String, String> errors) {
        super(status, message, timestamp, path);
        this.errors = errors;
    }

    public ValidationErrorResponse(int status, String message, LocalDateTime timestamp, Map<String, String> errors) {
        super(status, message, timestamp);
        this.errors = errors;
    }
}
