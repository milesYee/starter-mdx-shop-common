package com.mdx.common.base;


import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 通用返回信息
 *
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommonResponse<T> {
    private Integer code = 2000;
    private String message = "操作成功";
    private T body;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getBody() {
        return body;
    }

    public void setBody(T body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "CommonResponse{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", body=" + body +
                '}';
    }

    /**
     * 调用成功，无body返回
     *
     * @param <T> body类型
     * @return 返回信息
     */
    public static <T> CommonResponse<T> success() {
        return new CommonResponse<>();
    }

    /**
     * 调用失败，无 body 返回
     *
     * @param code 错误码
     * @return 返回信息
     */
    public static CommonResponse fail(Integer code, String message) {
        CommonResponse response = new CommonResponse();
        response.setCode(code);
        response.setMessage(message);
        return response;
    }

    /**
     * 调用成功，含 body
     *
     * @param body 返回内容
     * @param <T>  body 类型
     * @return 返回信息
     */
    public static <T> CommonResponse<T> success(T body) {
        CommonResponse<T> response = success();
        response.setBody(body);
        return response;
    }

}
