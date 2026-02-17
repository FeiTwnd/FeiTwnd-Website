package cc.feitwnd.service;

/**
 * Token服务
 */
public interface TokenService {
    
    /**
     * 创建并保存token
     */
    String createAndStoreToken(Long userId, Integer role);
    
    /**
     * 验证token有效性
     */
    boolean isValidToken(Long userId, String token);
    
    /**
     * 退出登录 - 删除token
     */
    void logout(Long userId, String token);
    
    /**
     * 退出登录 - 删除所有token
     */
    void logoutAll(Long userId);
}
