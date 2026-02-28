package cc.feitwnd.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;

/**
 * 安全扫描日志过滤器
 * 拦截常见的安全探测 / 漏洞扫描请求，直接返回 404 并记录到独立日志文件
 */
@Configuration
public class SecurityLogConfig {

    public static final Logger SECURITY_SCAN_LOG = LoggerFactory.getLogger("SECURITY_SCAN");

    /** 项目自身的合法路径前缀，命中则跳过检测 */
    private static final Set<String> ALLOWED_PREFIXES = Set.of(
            "/blog/", "/admin/", "/home/", "/cv/", "/health", "/ws"
    );

    /** 典型扫描路径中出现的后缀 */
    private static final Set<String> SCAN_EXTENSIONS = Set.of(
            ".env", ".php", ".bak", ".swp", ".old", ".save",
            ".config", ".ini", ".yml", ".yaml", ".properties",
            ".sql", ".tar.gz", ".zip", ".rar"
    );

    /** 典型扫描路径中出现的片段 */
    private static final Set<String> SCAN_SEGMENTS = Set.of(
            "/.env", "/.git", "/.svn", "/.htaccess", "/.htpasswd",
            "/wp-admin", "/wp-login", "/wp-content", "/wp-includes", "/wordpress",
            "/phpmyadmin", "/phpinfo", "/info.php", "/config.php",
            "/actuator", "/debug", "/console", "/druid",
            "/manager/html", "/solr", "/jenkins", "/struts",
            "/.aws", "/.docker", "/cgi-bin"
    );

    @Bean
    public OncePerRequestFilter securityScanLogFilter() {
        return new OncePerRequestFilter() {

            @Override
            protected void doFilterInternal(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain)
                    throws IOException, ServletException {

                String path = request.getRequestURI();
                String ip = getClientIp(request);

                // 先判断是否为项目合法路径，是则直接放行
                if (isAllowedPath(path)) {
                    org.slf4j.MDC.put("ip", ip);
                    try {
                        chain.doFilter(request, response);
                    } finally {
                        org.slf4j.MDC.remove("ip");
                    }
                    return;
                }

                // 检查是否为扫描请求
                if (isScanningRequest(path)) {
                    SECURITY_SCAN_LOG.warn("SCAN_ATTEMPT: {} {} from {} UA: {}",
                            request.getMethod(), path, ip, request.getHeader("User-Agent"));
                    response.setStatus(404);
                    response.setContentType("text/plain");
                    response.getWriter().write("Not Found");
                    return;
                }

                // 普通请求放行
                org.slf4j.MDC.put("ip", ip);
                try {
                    chain.doFilter(request, response);
                } finally {
                    org.slf4j.MDC.remove("ip");
                }
            }

            private boolean isAllowedPath(String path) {
                for (String prefix : ALLOWED_PREFIXES) {
                    if (path.startsWith(prefix)) {
                        return true;
                    }
                }
                return "/".equals(path);
            }

            private boolean isScanningRequest(String path) {
                String lower = path.toLowerCase();

                // 检查后缀
                for (String ext : SCAN_EXTENSIONS) {
                    if (lower.endsWith(ext)) {
                        return true;
                    }
                }
                // 检查路径片段
                for (String seg : SCAN_SEGMENTS) {
                    if (lower.contains(seg)) {
                        return true;
                    }
                }
                return false;
            }

            private String getClientIp(HttpServletRequest request) {
                String ip = request.getHeader("X-Forwarded-For");
                if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                    // X-Forwarded-For 可能包含多个 IP，取第一个
                    return ip.split(",")[0].trim();
                }
                ip = request.getHeader("X-Real-IP");
                if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                    return ip;
                }
                return request.getRemoteAddr();
            }
        };
    }
}
