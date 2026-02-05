package cc.feitwnd.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * IP地址工具类
 */
@Slf4j
public class IpUtil {
    // IP地址查询接口
    public static final String IP_API = "http://ip-api.com/json/";
    public static final String LANGUAGE = "zh-CN";

    // 获取真实IP地址
    public static String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        // 多级代理时，取第一个IP
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }

        return ip;
    }

    // 获取IP地址信息
    public static Map<String, String> getGeoInfo(String ip){
        Map<String,String> params = new HashMap<>();
        params.put("lang",LANGUAGE);
        String doneGet = HttpClientUtil.doGet(IP_API + ip, params);
        log.info("IP地址信息查询结果：{}",doneGet);
        // 封装返回结果
        Map<String, String> geoInfo = new HashMap<>();

        try {
            // 使用Jackson ObjectMapper解析JSON
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> jsonMap = mapper.readValue(doneGet, Map.class);

            // 提取需要的信息
            geoInfo.put("country", (String) jsonMap.getOrDefault("country", ""));
            geoInfo.put("province", (String) jsonMap.getOrDefault("regionName", ""));
            geoInfo.put("city", (String) jsonMap.getOrDefault("city", ""));
            geoInfo.put("latitude", String.valueOf(jsonMap.getOrDefault("lat", "")));
            geoInfo.put("longitude", String.valueOf(jsonMap.getOrDefault("lon", "")));

        } catch (Exception e) {
            log.error("解析IP地址信息失败", e);
        }
        return geoInfo;
    }
}
