package com.justdo.plug.member.global.exception;

import com.justdo.plug.member.global.response.code.ErrorReasonDto;
import com.justdo.plug.member.global.response.code.status.ErrorStatus;

public class ApiException extends RuntimeException{
    private final ErrorStatus errorStatus;

    public ApiException(ErrorStatus errorStatus) {
        super(errorStatus.getMessage());
        this.errorStatus = errorStatus;
    }

    public ErrorReasonDto getErrorReason() {
        return this.errorStatus.getReason();
    }

    public ErrorReasonDto getErrorReasonHttpStatus() {
        return this.errorStatus.getReasonHttpStatus();
    }
}
