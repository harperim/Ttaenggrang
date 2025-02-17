package com.ladysparks.ttaenggrang.global.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ErrorResponse {

    private int statusCode;
    private String message;
    private List<FieldErrorDetail> errors;

    @Getter
    @AllArgsConstructor
    public static class FieldErrorDetail {
        private String field;
        private String message;
    }

    // ✅ 예외 응답 생성 (필드 에러 포함)
    public static ErrorResponse of(int statusCode, String message, List<FieldErrorDetail> errors) {
        return new ErrorResponse(statusCode, message, errors);
    }

}

