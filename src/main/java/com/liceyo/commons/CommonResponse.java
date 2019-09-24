package com.liceyo.commons;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * CommonResponse
 * @description 通用返回
 * @author lichengyong
 * @date 2019/9/12 11:05
 * @version 1.0
 */
@Data
@AllArgsConstructor
public class CommonResponse<T> {
    public static final int DEFAULT_SUCCESS_CODE = 0;
    public static final int DEFAULT_FAILURE_CODE = 1;
    public static final String DEFAULT_SUCCESS_MSG = "Success";
    public static final String DEFAULT_FAILURE_MSG = "Failure";
    private int code;
    private String message;
    private T data;

    /**
     * CommonResponse
     * @description ok
     * @author lichengyong
     * @date 2019/9/12 11:05
     * @return com.liceyo.commons.CommonResponse
     * @version 1.0
     */
    public static CommonResponse ok() {
        return new CommonResponse<>(DEFAULT_SUCCESS_CODE, DEFAULT_SUCCESS_MSG, null);
    }

    /**
     * CommonResponse
     * @description ok
     * @author lichengyong
     * @date 2019/9/12 11:05
     * @param message ok的消息
     * @return com.liceyo.commons.CommonResponse
     * @version 1.0
     */
    public static CommonResponse okMsg(String message) {
        return new CommonResponse<>(DEFAULT_SUCCESS_CODE, message, null);
    }

    /**
     * CommonResponse
     * @description ok
     * @author lichengyong
     * @date 2019/9/12 11:05
     * @param data 数据
     * @return com.liceyo.commons.CommonResponse<R>
     * @version 1.0
     */
    public static <R> CommonResponse<R> ok(R data) {
        return new CommonResponse<>(DEFAULT_SUCCESS_CODE, DEFAULT_SUCCESS_MSG, data);
    }

    /**
     * CommonResponse
     * @description error
     * @author lichengyong
     * @date 2019/9/12 11:05
     * @return com.liceyo.commons.CommonResponse
     * @version 1.0
     */
    public static CommonResponse failure() {
        return failure(DEFAULT_FAILURE_CODE, DEFAULT_FAILURE_MSG);
    }

    /**
     * CommonResponse
     * @description error
     * @author lichengyong
     * @date 2019/9/12 11:05
     * @param message 错误信息
     * @return com.liceyo.commons.CommonResponse
     * @version 1.0
     */
    public static CommonResponse failure(String message) {
        return failure(DEFAULT_FAILURE_CODE, message);
    }

    /**
     * CommonResponse
     * @description error
     * @author lichengyong
     * @date 2019/9/12 11:05
     * @param code 错误码
     * @param message 错误信息
     * @return com.liceyo.commons.CommonResponse
     * @version 1.0
     */
    public static CommonResponse failure(int code, String message) {
        return new CommonResponse<>(code, message, null);
    }

    /**
     * CommonResponse
     * @description 如果data不为空返回ok,否则返回failure
     * @author lichengyong
     * @date 2019/9/12 11:16
     * @param data data
     * @param message 错误信息
     * @return com.liceyo.commons.CommonResponse
     * @version 1.0
     */
    public static <T> CommonResponse okIfNotNull(T data, String message) {
        if (data == null) {
            return failure(message);
        }
        return ok(data);
    }

}
