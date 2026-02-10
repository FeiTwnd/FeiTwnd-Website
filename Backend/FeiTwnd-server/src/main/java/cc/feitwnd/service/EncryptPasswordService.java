package cc.feitwnd.service;

/**
 * 密码加密服务
 */
public interface EncryptPasswordService {
    
    /**
     * 计算密码+盐的哈希
     * @param password
     * @param salt
     * @return
     * @throws Exception
     */
    String hashPassword(String password, String salt) throws Exception;
}
