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
            geoInfo.put("province", stripRegionSuffix((String) jsonMap.getOrDefault("regionName", "")));
            geoInfo.put("city", stripCitySuffix((String) jsonMap.getOrDefault("city", "")));
            geoInfo.put("latitude", String.valueOf(jsonMap.getOrDefault("lat", "")));
            geoInfo.put("longitude", String.valueOf(jsonMap.getOrDefault("lon", "")));

        } catch (Exception e) {
            log.error("解析IP地址信息失败", e);
        }
        return geoInfo;
    }

    /**
     * 去掉省/自治区等行政区划后缀
     * 例：广东省→广东，广西壮族自治区→广西，内蒙古自治区→内蒙古，香港特别行政区→香港
     */
    private static String stripRegionSuffix(String region) {
        if (region == null || region.isEmpty()) return region;
        return region
                .replaceAll("壮族自治区|维吾尔自治区|回族自治区|自治区|特别行政区|省$", "");
    }

    /**
     * 去掉市/地区等后缀
     * 例：广州市→广州，哈尔滨市→哈尔滨
     * 注意：保留两字城市名（如"吉市"不去"市"，但实际几乎不存在）
     */
    private static String stripCitySuffix(String city) {
        if (city == null || city.isEmpty()) return city;
        // 只有长度>2时才去掉"市"，避免把"X市"这样的短名变成单字
        if (city.length() > 2 && city.endsWith("市")) {
            return city.substring(0, city.length() - 1);
        }
        return city;
    }
}
