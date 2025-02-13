package com.ladysparks.ttaenggrang.global.response;

import com.ladysparks.ttaenggrang.domain.news.dto.NewsDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    private int statusCode;
    private String message;
    private T data;


    // ✅ 200 OK 응답 생성
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, "요청이 성공적으로 처리되었습니다.", data);
    }

    // ✅ 커스텀 메시지가 있는 200 OK 응답
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(200, message, data);
    }

    // ✅ 201 Created 응답 생성
    public static <T> ApiResponse<T> created(T data) {
        return new ApiResponse<>(201, "리소스가 성공적으로 생성되었습니다.", data);
    }

    public static <T> ApiResponse<T> created(String message, T data) {
        return new ApiResponse<>(201, message, data);
    }

    // ✅ 예외 응답 생성 (error 메시지 포함)
    public static <T> ApiResponse<T> error(int statusCode, String message, T data) {
        return new ApiResponse<>(statusCode, message, data);
    }
}
