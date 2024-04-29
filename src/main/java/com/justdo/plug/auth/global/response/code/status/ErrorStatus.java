package com.justdo.plug.auth.global.response.code.status;

import com.justdo.plug.auth.global.response.code.BaseErrorCode;
import com.justdo.plug.auth.global.response.code.ErrorReasonDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseErrorCode {

    // 일반 응답
    _INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다."),
    _BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON400", "잘못된 요청입니다."),
    _UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "COMMON401", "인증이 필요합니다."),
    _FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "금지된 요청입니다."),

    // 카카오 api 호출 응답
    _KAKAO_TOKEN_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "KAKAO_001", "카카오 토큰을 가져오는 중에 오류가 발생했습니다."),
    _KAKAO_USER_INFO_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "KAKAO_002", "카카오 사용자 정보를 가져오는 중에 오류가 발생했습니다."),


    // 레디스 응답
    _REDIS_OPERATION_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,"REDIS_001","레디스 데이터 접근 중에 오류가 발생했습니다."),
    // 멤버 관련 응답
    _MEMBER_NOT_FOUND_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "MEMBER_001", "해당 사용자를 찾을 수 없습니다."),

    // jwt
    _INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "JWT_001", "유효하지 않은 리프레시 토큰입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;


    @Override
    public ErrorReasonDto getReason() {
        return ErrorReasonDto.builder()
                .isSuccess(false)
                .code(code)
                .message(message)
                .build();
    }

    @Override
    public ErrorReasonDto getReasonHttpStatus() {
        return ErrorReasonDto.builder()
                .httpStatus(httpStatus)
                .isSuccess(false)
                .code(code)
                .message(message)
                .build();
    }
}