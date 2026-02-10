package cc.feitwnd.service;

/**
 * 封禁服务
 */
public interface BlockService {
    
    /**
     * 检查缓存是否有被封禁记录
     * @param fingerprint
     */
    void checkIfBlocked(String fingerprint);
    
    /**
     * 检查请求频率
     * @param fingerprint
     * @param ip
     */
    void checkRateLimit(String fingerprint, String ip);
}
