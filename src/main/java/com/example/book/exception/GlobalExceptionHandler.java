package com.example.book.exception;


import com.example.book.dto.ResponseDTO.ApiResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleInvalidArgument(MethodArgumentNotValidException exception) {
        Map<String, String> errorMap = new HashMap<>();
        exception.getBindingResult().getFieldErrors().forEach(error -> {
            errorMap.put(error.getField(), error.getDefaultMessage());
        });
        ApiResponse<Object> res = new ApiResponse<>();
        res.setCode(HttpStatus.BAD_REQUEST.value());
        res.setMessage("Validation failed");
        res.setData(errorMap);
        return ResponseEntity.badRequest().body(res);
    }

    @ExceptionHandler(value = DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Object>> DataIntegrityViolationException(DataIntegrityViolationException e) {
        ApiResponse<Object> res = new ApiResponse<>();
        res.setCode(HttpStatus.BAD_REQUEST.value());
        res.setMessage(e.getMessage());
        res.setData("DataIntegrityViolationException");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
    }

    @ExceptionHandler(value = IdInvalidException.class)
    public ResponseEntity<ApiResponse<Object>> idInvalidException(IdInvalidException e) {
        ApiResponse<Object> res = new ApiResponse<>();
        res.setCode(HttpStatus.BAD_REQUEST.value());
        res.setMessage(e.getMessage());
        res.setData("IdInvalidException");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
    }

    @ExceptionHandler(value = ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> ResourceNotFoundException(ResourceNotFoundException e) {
        ApiResponse<Object> res = new ApiResponse<>();
        res.setCode(HttpStatus.NOT_FOUND.value());
        res.setMessage(e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(res);
    }

    @ExceptionHandler(value = BusinessException.class)
    public ResponseEntity<ApiResponse<Object>> LogicException(BusinessException e) {
        ApiResponse<Object> res = new ApiResponse<>();
        res.setCode(HttpStatus.BAD_REQUEST.value());
        res.setMessage(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
    }

    @ExceptionHandler(value = LoginException.class)
    public ResponseEntity<ApiResponse<Object>> LoginException(LoginException e) {
        ApiResponse<Object> res = new ApiResponse<>();
        res.setCode(HttpStatus.BAD_REQUEST.value());
        res.setMessage(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
    }

    @ExceptionHandler(value = NullValueException.class)
    public ResponseEntity<ApiResponse<Object>> NullValueException(NullValueException e) {
        ApiResponse<Object> res = new ApiResponse<>();
        res.setCode(HttpStatus.BAD_REQUEST.value());
        res.setMessage(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
    }

    @ExceptionHandler(value = UnauthorizedException.class)
    public ResponseEntity<ApiResponse<Object>> UnauthorizedException(UnauthorizedException e) {
        ApiResponse<Object> res = new ApiResponse<>();
        res.setCode(HttpStatus.FORBIDDEN.value());
        res.setMessage(e.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(res);
    }
}
