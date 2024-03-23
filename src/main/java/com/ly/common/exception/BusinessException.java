package com.ly.common.exception;

import com.ly.common.result.IResultCode;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException{

    public IResultCode resultCode;

    public BusinessException(IResultCode errorCode) {
        super(errorCode.getMsg());
        this.resultCode = errorCode;
    }

    public BusinessException(String message){
        super(message);
    }

    public BusinessException(String message, Throwable cause){
        super(message, cause);
    }

    public BusinessException(Throwable cause){
        super(cause);
    }

}
