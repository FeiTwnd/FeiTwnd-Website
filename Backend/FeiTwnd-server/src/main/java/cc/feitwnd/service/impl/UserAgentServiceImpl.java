package cc.feitwnd.service.impl;

import cc.feitwnd.service.UserAgentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * UserAgent解析服务实现
 */
@Slf4j
@Service
public class UserAgentServiceImpl implements UserAgentService {

    /**
     * 获取操作系统名称
     * @param userAgent
     * @return
     */
    public String getOsName(String userAgent) {
        if (userAgent == null || userAgent.isEmpty()) {
            return "Unknown";
        }
        
        String ua = userAgent.toLowerCase();
        
        // Windows系列
        if (ua.contains("windows nt 10.0")) {
            return "Windows";
        } else if (ua.contains("windows nt 6.3")) {
            return "Windows";
        } else if (ua.contains("windows nt 6.2")) {
            return "Windows";
        } else if (ua.contains("windows nt 6.1")) {
            return "Windows";
        } else if (ua.contains("windows")) {
            return "Windows";
        }
        
        // macOS系列
        if (ua.contains("mac os x") || ua.contains("macintosh")) {
            return "Macos";
        }
        
        // Linux系列
        if (ua.contains("linux")) {
            // Android基于Linux，但优先识别为Android
            if (ua.contains("android")) {
                return "Android";
            }
            return "Linux";
        }
        
        // 移动设备
        if (ua.contains("android")) {
            return "Android";
        }
        if (ua.contains("iphone") || ua.contains("ipad")) {
            return "Ios";
        }
        
        // 其他系统
        if (ua.contains("freebsd")) {
            return "Freebsd";
        }
        if (ua.contains("ubuntu")) {
            return "Ubuntu";
        }
        
        return "Unknown";
    }

    /**
     * 获取浏览器名称
     * @param userAgent
     * @return
     */
    public String getBrowserName(String userAgent) {
        if (userAgent == null || userAgent.isEmpty()) {
            return "Unknown";
        }
        
        String ua = userAgent.toLowerCase();
        
        // 微信内置浏览器（UA包含Chrome，必须优先判断）
        if (ua.contains("micromessenger")) {
            return "Wechat";
        }
        
        // QQ内置浏览器（UA包含Chrome，必须优先判断）
        if (ua.contains("qq/")) {
            return "QQ";
        }
        
        // Edge浏览器 (新版基于Chromium)
        if (ua.contains("edg/") || ua.contains("edge/")) {
            return "Edge";
        }
        
        // Opera浏览器（UA包含Chrome，需在Chrome之前判断）
        if (ua.contains("opr/") || ua.contains("opera/")) {
            return "Opera";
        }
        
        // Chrome浏览器 (需在Safari之前判断,因为Chrome的UA包含Safari)
        if (ua.contains("chrome/") && !ua.contains("edg")) {
            return "Chrome";
        }
        
        // Safari浏览器
        if (ua.contains("safari/") && !ua.contains("chrome")) {
            return "Safari";
        }
        
        // Firefox浏览器
        if (ua.contains("firefox/")) {
            return "Firefox";
        }
        
        // IE浏览器
        if (ua.contains("msie") || ua.contains("trident/")) {
            return "Ie";
        }
        
        return "Unknown";
    }
}
