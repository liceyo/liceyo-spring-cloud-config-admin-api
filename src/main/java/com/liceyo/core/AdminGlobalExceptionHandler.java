package com.liceyo.core;

import com.liceyo.commons.CommonResponse;
import com.liceyo.commons.ConfigAdminApiException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletResponse;

/**
 * AdminGlobalExceptionHandler
 * @description 统一异常处理
 * @author lichengyong
 * @date 2019/9/12 18:18
 * @version 1.0
 */
@RestControllerAdvice("com.liceyo.api")
public class AdminGlobalExceptionHandler {
    private static Logger logger = LogManager.getLogger(AdminGlobalExceptionHandler.class);

    /**
     * AdminGlobalExceptionHandler
     * @description 异常处理
     * @author lichengyong
     * @date 2019/9/12 18:19
     * @param e 异常
     * @param response response
     * @return com.liceyo.commons.CommonResponse
     * @version 1.0
     */
    @ExceptionHandler(Exception.class)
    public CommonResponse handleException(Exception e, HttpServletResponse response) {
        logger.error(e);
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        return CommonResponse.failure(2,e.getMessage());
    }

    /**
     * AdminGlobalExceptionHandler
     * @description 异常处理
     * @author lichengyong
     * @date 2019/9/12 18:20
     * @param e 自定义异常
     * @param response response
     * @return com.liceyo.commons.CommonResponse
     * @version 1.0
     */
    @ExceptionHandler(ConfigAdminApiException.class)
    public CommonResponse handleException(ConfigAdminApiException e, HttpServletResponse response) {
        logger.error(e);
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        return CommonResponse.failure(e.getMessage());
    }
}
