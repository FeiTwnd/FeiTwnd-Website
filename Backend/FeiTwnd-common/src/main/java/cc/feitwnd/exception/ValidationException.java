package cc.feitwnd.exception;

/**
 * 参数校验异常
 */
public class ValidationException extends BaseException {
    public ValidationException() {
    }
    
    public ValidationException(String msg) {
        super(msg);
    }
}
