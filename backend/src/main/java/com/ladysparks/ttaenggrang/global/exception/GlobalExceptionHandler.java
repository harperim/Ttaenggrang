package com.ladysparks.ttaenggrang.global.exception;

import com.ladysparks.ttaenggrang.global.response.ErrorResponse;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // ✅ 400 - 잘못된 요청 (입력값 오류)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        BindingResult bindingResult = ex.getBindingResult();
        List<ErrorResponse.FieldErrorDetail> errors = bindingResult.getFieldErrors().stream()
                .map(fieldError -> new ErrorResponse.FieldErrorDetail(fieldError.getField(), fieldError.getDefaultMessage()))
                .collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of(
                        400,
                        "잘못된 요청입니다. 입력 값을 확인해주세요.",
                        errors
                ));
    }

    // ✅ 401 - 인증되지 않은 사용자 (토큰 인증 실패)
    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedException(SecurityException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ErrorResponse.of(401, "인증이 필요합니다. 로그인 후 다시 시도해주세요.", null));
    }

    // ✅ 403 - 권한 없음
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ErrorResponse.of(403, "접근이 거부되었습니다. 권한을 확인해주세요.", null));
    }

    // ✅ 404 - 리소스를 찾을 수 없음
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(EntityNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.of(
                        404,
                        Optional.ofNullable(ex.getMessage()).orElse("요청하신 리소스를 찾을 수 없습니다."),
                        null
                ));
    }

    // ✅ 409 - 데이터 중복 오류
    @ExceptionHandler(IllegalStateException.class) // 중복 데이터 처리할 때 발생할 가능성 있음
    public ResponseEntity<ErrorResponse> handleConflictException(IllegalStateException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ErrorResponse.of(409, "이미 존재하는 데이터입니다. 중복을 확인해주세요.", null));
    }

    // ✅ 422 - 유효하지 않은 데이터 (데이터 검증 실패)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleUnprocessableEntityException(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(ErrorResponse.of(
                        422,
                        Optional.ofNullable(ex.getMessage()).orElse("요청 데이터를 처리할 수 없습니다. 입력 값을 확인해주세요."),
                        null
                ));
    }

    // ✅ 500 - 서버 내부 오류
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.of(
                        500,
                        Optional.ofNullable(ex.getMessage()).orElse("서버 내부 오류가 발생했습니다. 관리자에게 문의하세요."),
                        null
                ));
    }

    // ✅ 502 - 잘못된 게이트웨이 응답
    @ExceptionHandler(org.springframework.web.client.HttpServerErrorException.BadGateway.class)
    public ResponseEntity<ErrorResponse> handleBadGatewayException(Exception ex) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(ErrorResponse.of(502, "서버가 잘못된 응답을 받았습니다.", null));
    }

    // ✅ 503 - 서버 점검 중
    @ExceptionHandler(org.springframework.web.client.HttpServerErrorException.ServiceUnavailable.class)
    public ResponseEntity<ErrorResponse> handleServiceUnavailableException(Exception ex) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(ErrorResponse.of(503, "현재 서버가 점검 중입니다. 잠시 후 다시 시도해주세요.", null));
    }

    // ✅ 504 - 요청 시간 초과
    @ExceptionHandler(org.springframework.web.client.HttpServerErrorException.GatewayTimeout.class)
    public ResponseEntity<ErrorResponse> handleGatewayTimeoutException(Exception ex) {
        return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT)
                .body(ErrorResponse.of(504, "서버 요청 시간이 초과되었습니다. 다시 시도해주세요.", null));
    }

}
