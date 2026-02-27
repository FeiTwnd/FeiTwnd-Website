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
            geoInfo.put("province", stripAdminSuffix((String) jsonMap.getOrDefault("regionName", "")));
            geoInfo.put("city", stripAdminSuffix((String) jsonMap.getOrDefault("city", "")));
            geoInfo.put("latitude", String.valueOf(jsonMap.getOrDefault("lat", "")));
            geoInfo.put("longitude", String.valueOf(jsonMap.getOrDefault("lon", "")));

        } catch (Exception e) {
            log.error("解析IP地址信息失败", e);
        }
        return geoInfo;
    }

    /**
     * 去掉行政区划后缀（省、市、自治区、特别行政区）
     * 每个字段都独立校验"省"和"市"后缀
     */
    private static String stripAdminSuffix(String name) {
        if (name == null || name.isEmpty()) return name;
        // 先去除复杂的行政区划后缀
        name = name.replaceAll("壮族自治区|维吾尔自治区|回族自治区|自治区|特别行政区", "");
        // 再去除末尾的"省"或"市"（保证去除后至少保留 1 个字符）
        if (name.length() > 1 && (name.endsWith("省") || name.endsWith("市"))) {
            name = name.substring(0, name.length() - 1);
        }
        return name;
    }
}
