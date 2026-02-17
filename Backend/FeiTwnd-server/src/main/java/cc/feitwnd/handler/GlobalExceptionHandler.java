package cc.feitwnd.handler;

import cc.feitwnd.constant.MessageConstant;
import cc.feitwnd.exception.BaseException;
import cc.feitwnd.exception.BlockedException;
import cc.feitwnd.exception.GuestReadOnlyException;
import cc.feitwnd.exception.TokenException;
import cc.feitwnd.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.stream.Collectors;

/**
 * 全局异常处理器，处理项目中抛出的业务异常
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 捕获业务异常
     * @param ex
     * @return
     */
    @ExceptionHandler
    public Result exceptionHandler(BaseException ex){
        log.error("业务异常：{}", ex.getMessage());
        return Result.error(ex.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Result exceptionHandler(TokenException ex){
        log.error("令牌异常：{}", ex.getMessage());
        return Result.error(ex.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Result exceptionHandler(BlockedException ex){
        log.error("封禁异常：{}", ex.getMessage());
        return Result.error(ex.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Result exceptionHandler(GuestReadOnlyException ex){
        log.error("游客只读异常：{}", ex.getMessage());
        return Result.error(ex.getMessage());
    }

    /**
     * 捕获参数校验异常（@Valid校验失败）
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result exceptionHandler(MethodArgumentNotValidException ex){
        String errorMsg = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));
        log.error("参数校验异常：{}", errorMsg);
        return Result.error(errorMsg);
    }

    @ExceptionHandler
    public Result exceptionHandler(SQLIntegrityConstraintViolationException ex){
        String message = ex.getMessage();
        if(message.contains("Duplicate entry")){
            String [] split = message.split(" ");
            String username = split[2];
            String msg = username + MessageConstant.ALREADY_EXIST;
            return Result.error(msg);
        }else{
            return Result.error(MessageConstant.UNKNOWN_ERROR);
        }
    }

    /**
     * 请求方法不支持异常
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public Result exceptionHandler(HttpRequestMethodNotSupportedException ex){
        log.error("请求方法不支持：{}", ex.getMessage());
        return Result.error("不支持的请求方法：" + ex.getMethod());
    }

    /**
     * 缺少请求参数异常
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result exceptionHandler(MissingServletRequestParameterException ex){
        log.error("缺少请求参数：{}", ex.getMessage());
        return Result.error("缺少必要参数：" + ex.getParameterName());
    }

    /**
     * 文件上传大小超限异常
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result exceptionHandler(MaxUploadSizeExceededException ex){
        log.error("文件上传大小超限：{}", ex.getMessage());
        return Result.error("上传文件大小超过限制");
    }

    /**
     * 兜底异常处理
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result exceptionHandler(Exception ex){
        log.error("未知异常：", ex);
        return Result.error(MessageConstant.UNKNOWN_ERROR);
    }

}
