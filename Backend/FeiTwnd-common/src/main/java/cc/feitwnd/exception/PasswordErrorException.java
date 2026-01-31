package cc.feitwnd.exception;

import cc.feitwnd.constant.MessageConstant;

public class PasswordErrorException extends BaseException{
    public PasswordErrorException() {
    }
    public PasswordErrorException(String msg) {
        super(msg);
    }
}
