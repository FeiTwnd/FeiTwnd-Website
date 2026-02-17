package cc.feitwnd.exception;

/**
 * 游客只读异常 - 游客账号尝试执行写操作时抛出
 */
public class GuestReadOnlyException extends RuntimeException {
    public GuestReadOnlyException() {
    }
    public GuestReadOnlyException(String msg) {
        super(msg);
    }
}
