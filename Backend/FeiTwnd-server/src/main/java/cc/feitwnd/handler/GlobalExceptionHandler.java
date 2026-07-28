package cc.feitwnd.handler;

import cc.feitwnd.constant.MessageConstant;
import cc.feitwnd.exception.BaseException;
import cc.feitwnd.exception.BlockedException;
import cc.feitwnd.exception.GuestReadOnlyException;
import cc.feitwnd.exception.TokenException;
import cc.feitwnd.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;

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
        log.warn("业务异常：{}", ex.getMessage());
        return Result.error(ex.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Result exceptionHandler(TokenException ex){
        log.warn("令牌异常：{}", ex.getMessage());
        return Result.error(ex.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Result exceptionHandler(BlockedException ex){
        log.warn("封禁异常：{}", ex.getMessage());
        return Result.error(ex.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Result exceptionHandler(GuestReadOnlyException ex){
        log.warn("游客只读异常：{}", ex.getMessage());
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
        log.warn("参数校验异常：{}", errorMsg);
        return Result.error(errorMsg);
    }

    /**
     * 请求体JSON格式错误（例如数组传对象、字段格式非法）
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result exceptionHandler(HttpMessageNotReadableException ex){
        log.warn("请求体解析异常：{}", ex.getMostSpecificCause().getMessage());
        return Result.error("请求体格式错误");
    }

    /**
     * 路径参数/查询参数类型不匹配
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result exceptionHandler(MethodArgumentTypeMismatchException ex){
        log.warn("参数类型不匹配：{}={}, 目标类型={}",
                ex.getName(), ex.getValue(), ex.getRequiredType() == null ? "unknown" : ex.getRequiredType().getSimpleName());
        return Result.error("参数类型错误：" + ex.getName());
    }

    /**
     * 数据库唯一约束/外键约束异常（SQL 层直接抛出）
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result exceptionHandler(SQLIntegrityConstraintViolationException ex){
        log.warn("数据库约束异常：{}", ex.getMessage());
        return Result.error(resolveConstraintMessage(ex));
    }

    /**
     * 数据库唯一约束/外键约束异常（Spring 翻译后抛出）
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result exceptionHandler(DataIntegrityViolationException ex){
        log.warn("数据库约束异常：{}", ex.getMostSpecificCause() != null
                ? ex.getMostSpecificCause().getMessage() : ex.getMessage());
        return Result.error(resolveConstraintMessage(ex));
    }

    /**
     * 请求方法不支持异常
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public Result exceptionHandler(HttpRequestMethodNotSupportedException ex){
        log.warn("请求方法不支持：{}", ex.getMessage());
        return Result.error("不支持的请求方法：" + ex.getMethod());
    }

    /**
     * 缺少请求参数异常
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result exceptionHandler(MissingServletRequestParameterException ex){
        log.warn("缺少请求参数：{}", ex.getMessage());
        return Result.error("缺少必要参数：" + ex.getParameterName());
    }

    /**
     * 请求路径不存在异常
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Result exceptionHandler(NoHandlerFoundException ex){
        log.warn("请求路径不存在：{} {}", ex.getHttpMethod(), ex.getRequestURL());
        return Result.error("请求地址不存在");
    }

    /**
     * 文件上传大小超限异常
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result exceptionHandler(MaxUploadSizeExceededException ex){
        log.warn("文件上传大小超限：{}", ex.getMessage());
        return Result.error("上传文件大小超过限制（30MB）");
    }

    /**
     * 兜底异常处理
     * <p>
     * MyBatis 的约束异常在部分场景下顶层类型是未被 Spring 翻译的 PersistenceException，
     * 会直接命中本兜底分支。这里先遍历异常链尝试识别数据库约束冲突，
     * 识别不到才返回带异常类型的可诊断信息，便于排查。
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result exceptionHandler(Exception ex){
        String constraintMessage = matchConstraintMessage(ex);
        if (constraintMessage != null) {
            log.warn("数据库约束异常（兜底捕获）：{}", ex.getMessage());
            return Result.error(constraintMessage);
        }
        log.error("未知异常（{}）：", ex.getClass().getName(), ex);
        return Result.error(MessageConstant.UNKNOWN_ERROR + "：" + ex.getClass().getSimpleName());
    }

    /**
     * 解析约束异常的友好信息（已知是约束异常，从异常链中提取）
     */
    private String resolveConstraintMessage(Throwable ex) {
        String message = matchConstraintMessage(ex);
        return message != null ? message : "数据校验失败，请检查提交的内容";
    }

    /**
     * 遍历异常链，识别数据库唯一/外键约束冲突并返回友好信息；无法识别返回 null。
     * 遍历整条 cause 链，兼容异常已被 Spring 翻译或仍为原始 MyBatis/JDBC 异常两种情况。
     */
    private String matchConstraintMessage(Throwable ex) {
        Throwable current = ex;
        int depth = 0;
        while (current != null && depth++ < 10) {
            String message = current.getMessage();
            if (message != null) {
                if (message.contains("Duplicate entry")) {
                    return resolveDuplicateKeyMessage(message);
                }
                if (message.contains("foreign key constraint fails")) {
                    return "操作失败：存在关联数据，请先处理关联项";
                }
                if (message.contains("cannot be null")) {
                    return "必填字段不能为空";
                }
            }
            current = current.getCause();
        }
        return null;
    }

    private String resolveDuplicateKeyMessage(String message) {
        String friendly = mapDuplicateKeyToMessage(message);
        if (friendly != null) {
            return friendly;
        }
        String[] split = message.split(" ");
        String value = split.length > 2 ? split[2] : "";
        return value + MessageConstant.ALREADY_EXIST;
    }

    private String mapDuplicateKeyToMessage(String message) {
        String key = extractDuplicateKey(message);
        if (key == null) {
            return null;
        }
        if (key.contains("article.slug") || key.contains("slug")) {
            return "Slug 已存在";
        }
        if (key.contains("system_config.config_key") || key.contains("config_key")) {
            return "配置键已存在";
        }
        if (key.contains("uk_tag_name")) {
            return "标签名称已存在";
        }
        if (key.contains("uk_tag_slug")) {
            return "标签 Slug 已存在";
        }
        if (key.contains("uk_article_tag")) {
            return "文章标签已存在";
        }
        if (key.contains("uk_article_visitor")) {
            return "已点赞";
        }
        if (key.contains("uk_visitor_fingerprint")) {
            return "访客已存在";
        }
        return null;
    }

    private String extractDuplicateKey(String message) {
        int keyIndex = message.indexOf("for key '");
        if (keyIndex < 0) {
            return null;
        }
        int start = keyIndex + "for key '".length();
        int end = message.indexOf("'", start);
        if (end <= start) {
            return null;
        }
        return message.substring(start, end);
    }

}
