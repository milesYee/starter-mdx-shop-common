package com.mdx.common.exception;

/**
 * 业务异常
 *
 */

public class BizException extends RuntimeException {
    /**
     * 错误码
     */
    private Integer errorCode = 3000;

    public BizException(String message) {
        super(message);
    }

    public BizException(Integer errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }


    public BizException(String message, Throwable e) {
        super(message, e);
    }

    public BizException(Integer errorCode, String message, Throwable e) {
        super(message, e);
        this.errorCode = errorCode;
    }


    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

    @Override
    public String toString() {
        return "BizException{" +
                "errorCode=" + errorCode +
                '}';
    }
}
