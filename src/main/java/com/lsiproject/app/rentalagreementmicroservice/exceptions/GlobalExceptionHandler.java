package com.lsiproject.app.rentalagreementmicroservice.exceptions;

import com.lsiproject.app.rentalagreementmicroservice.dtos.ErrorResponseDto;
import jakarta.servlet.http.HttpServletRequest; // Important for getting the path
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * This method catches any ResponseStatusException thrown in your Service or Controller.
     */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponseDto> handleResponseStatusException(
            ResponseStatusException ex, HttpServletRequest request) {

        // Build the custom error object
        ErrorResponseDto errorResponse = new ErrorResponseDto(
                LocalDateTime.now(),
                ex.getStatusCode().value(),
                ex.getStatusCode().toString(), // e.g., "400 BAD_REQUEST"
                ex.getReason(),               // This contains your custom message!
                request.getRequestURI()       // The URL that was called
        );

        // Return the JSON with the correct HTTP status code
        return new ResponseEntity<>(errorResponse, ex.getStatusCode());
    }
}