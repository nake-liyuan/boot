package com.ly.common.exception;


import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.ly.common.result.Result;
import com.ly.common.result.ResultCode;
import jakarta.servlet.ServletException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.TypeMismatchException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.sql.SQLSyntaxErrorException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 进程异常
     */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public <T> Result<T> processException(BindException e) {
        log.error("BindException:{}", e.getMessage());
        String msg = e.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.joining("；"));
        return Result.failed(ResultCode.PARAM_ERROR, msg);
    }

    /**
     * RequestParam参数的校验
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public <T> Result<T> processException(ConstraintViolationException e) {
        log.error("ConstraintViolationException:{}", e.getMessage());
        String msg = e.getConstraintViolations().stream().map(ConstraintViolation::getMessage).collect(Collectors.joining("；"));
        return Result.failed(ResultCode.PARAM_ERROR, msg);
    }

    /**
     * RequestBody参数的校验
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public <T> Result<T> processException(MethodArgumentNotValidException e) {
        log.error("MethodArgumentNotValidException:{}", e.getMessage());
        String msg = e.getBindingResult().getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.joining("；"));
        return Result.failed(ResultCode.PARAM_ERROR, msg);
    }

    /**
     * 请求资源不存在
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public <T> Result<T> processException(NoHandlerFoundException e) {
        log.error(e.getMessage(), e);
        return Result.failed(ResultCode.RESOURCE_NOT_FOUND);
    }

    /**
     * 缺少RequestParameter异常
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public <T> Result<T> processException(MissingServletRequestParameterException e) {
        log.error(e.getMessage(), e);
        return Result.failed(ResultCode.PARAM_IS_NULL);
    }

    /**
     * 方法参数类型不匹配异常
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public <T> Result<T> processException(MethodArgumentTypeMismatchException e) {
        log.error(e.getMessage(), e);
        return Result.failed(ResultCode.PARAM_ERROR, "类型错误");
    }

    /**
     * Servlet异常
     */
    @ExceptionHandler(ServletException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public <T> Result<T> processException(ServletException e) {
        log.error(e.getMessage(), e);
        return Result.failed(e.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public <T> Result<T> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("非法参数异常，异常原因：{}", e.getMessage(), e);
        return Result.failed(e.getMessage());
    }

    @ExceptionHandler(JsonProcessingException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public <T> Result<T> handleJsonProcessingException(JsonProcessingException e) {
        log.error("Json转换异常，异常原因：{}", e.getMessage(), e);
        return Result.failed(e.getMessage());
    }

    /**
     * Http 消息不可读异常
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public <T> Result<T> processException(HttpMessageNotReadableException e) {
        log.error(e.getMessage(), e);
        String errorMessage = "请求体不可为空";
        Throwable cause = e.getCause();
        if (cause != null) {
            errorMessage = convertMessage(cause);
        }
        return Result.failed(errorMessage);
    }

    /**
     * 类型不匹配异常
     */
    @ExceptionHandler(TypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public <T> Result<T> processException(TypeMismatchException e) {
        log.error(e.getMessage(), e);
        return Result.failed(e.getMessage());
    }

    /**
     * 错误的 SQL 语法异常(演示环境，)
     * denied to user(拒绝用户)
     * BadSqlGrammarException通常是由框架层抛出的，它可能包括但不限于语法错误。
     */
    @ExceptionHandler(BadSqlGrammarException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public <T> Result<T> handleBadSqlGrammarException(BadSqlGrammarException e) {
        log.error(e.getMessage(), e);
        String errorMsg = e.getMessage();
        if (StrUtil.isNotBlank(errorMsg) && errorMsg.contains("denied to user")) {
            return Result.failed(ResultCode.FORBIDDEN_OPERATION);
        } else {
            return Result.failed(e.getMessage());
        }
    }

    /**
     * SQL 语法错误异常
     */
    @ExceptionHandler(SQLSyntaxErrorException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public <T> Result<T> processSQLSyntaxErrorException(SQLSyntaxErrorException e) {
        log.error(e.getMessage(), e);
        return Result.failed(e.getMessage());
    }


    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public <T> Result<T> handleBizException(BusinessException e) {
        log.error("biz exception: {}", e.getMessage());
        if (e.getResultCode() != null) {
            return Result.failed(e.getResultCode());
        }
        return Result.failed(e.getMessage());
    }

    // @ExceptionHandler(Exception.class)
    // @ResponseStatus(HttpStatus.BAD_REQUEST)
    // public <T> Result<T> handleException(Exception e) throws Exception{
    //     // 将 Spring Security 异常继续抛出，以便交给自定义处理器处理
    //     if (e instanceof AccessDeniedException
    //             || e instanceof AuthenticationException) {
    //         throw e;
    //     }
    //     log.error("unknown exception: {}", e.getMessage());
    //     e.printStackTrace();
    //     return Result.failed(e.getLocalizedMessage());
    // }


    /**
     * 传参类型错误时，用于消息转换
     *
     * @param throwable 异常
     * @return 错误信息
     */
    private String convertMessage(Throwable throwable) {
        String error = throwable.toString();
        String regulation = "\\[\"(.*?)\"]+";
        Pattern pattern = Pattern.compile(regulation);
        Matcher matcher = pattern.matcher(error);
        String group = "";
        if (matcher.find()) {
            String matchString = matcher.group();
            matchString = matchString.replace("[", "").replace("]", "");
            matchString = "%s字段类型错误".formatted(matchString.replaceAll("\"", ""));
            group += matchString;
        }
        return group;
    }
}
