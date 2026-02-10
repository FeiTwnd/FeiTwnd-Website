package cc.feitwnd.service;

/**
 * UserAgent解析服务
 */
public interface UserAgentService {
    
    /**
     * 解析UserAgent获取操作系统名称
     * @param userAgent UserAgent字符串
     * @return 操作系统名称(首字母大写)
     */
    String getOsName(String userAgent);
    
    /**
     * 解析UserAgent获取浏览器名称
     * @param userAgent UserAgent字符串
     * @return 浏览器名称(首字母大写)
     */
    String getBrowserName(String userAgent);
}
