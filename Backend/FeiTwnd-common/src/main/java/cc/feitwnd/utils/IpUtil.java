package cc.feitwnd.utils;

import lombok.extern.slf4j.Slf4j;
import jakarta.servlet.http.HttpServletRequest;
import org.lionsoul.ip2region.xdb.LongByteArray;
import org.lionsoul.ip2region.xdb.Searcher;
import org.lionsoul.ip2region.xdb.Version;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * IP地址工具类
 */
@Slf4j
public class IpUtil {
    // ip2region 离线地址库（resources 路径）
    private static final String XDB_PATH = "/ip2region/ip2region_v4.xdb";
    // 基于全量缓存的 Searcher，线程安全可全局复用
    private static Searcher searcher;

    static {
        try (InputStream is = IpUtil.class.getResourceAsStream(XDB_PATH)) {
            LongByteArray cBuff = Searcher.loadContentFromInputStream(is);
            searcher = Searcher.newWithBuffer(Version.IPv4, cBuff);
        } catch (Exception e) {
            log.error("ip2region地址库加载失败", e);
        }
    }

    // 获取真实IP地址（兼容CDN/反向代理）
    public static String getClientIp(HttpServletRequest request) {
        // CDN专用头（优先级最高）
        String ip = request.getHeader("CF-Connecting-IP");      // Cloudflare
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("True-Client-IP");            // Cloudflare Enterprise / Akamai
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Ali-CDN-Real-IP");           // 阿里云CDN
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");                 // Nginx / 通用CDN
        }
        // 标准代理头
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Forwarded-For");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        // 多级代理时，取第一个IP（即真实客户端IP）
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }

        return ip;
    }

    // 获取IP地址信息
    // ip2region 返回格式：国家|省份|城市|ISP|国家代码
    public static Map<String, String> getGeoInfo(String ip){
        Map<String, String> geoInfo = new HashMap<>();
        geoInfo.put("country", "");
        geoInfo.put("province", "");
        geoInfo.put("city", "");
        if (searcher == null || ip == null || ip.isEmpty()) {
            return geoInfo;
        }
        try {
            String region = searcher.search(ip);
            log.info("IP地址信息查询结果：{}", region);
            if (region != null && !region.isEmpty()) {
                String[] parts = region.split("\\|");
                geoInfo.put("country", normalizeField(parts.length > 0 ? parts[0] : ""));
                geoInfo.put("province", stripAdminSuffix(normalizeField(parts.length > 1 ? parts[1] : "")));
                geoInfo.put("city", stripAdminSuffix(normalizeField(parts.length > 2 ? parts[2] : "")));
            }
        } catch (Exception e) {
            log.error("解析IP地址信息失败", e);
        }
        return geoInfo;
    }

    /**
     * ip2region 未知字段返回 0 或 Reserved，统一置空
     */
    private static String normalizeField(String field) {
        if (field == null || field.isEmpty() || "0".equals(field) || "Reserved".equalsIgnoreCase(field)) {
            return "";
        }
        return field;
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
