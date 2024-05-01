package com.justdo.plug.auth.global.exception;

import com.justdo.plug.auth.global.response.code.ErrorReasonDto;
import com.justdo.plug.auth.global.response.code.status.ErrorStatus;

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
