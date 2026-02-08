package cc.feitwnd.service.impl;

import cc.feitwnd.constant.MessageConstant;
import cc.feitwnd.constant.StatusConstant;
import cc.feitwnd.dto.VisitorPageQueryDTO;
import cc.feitwnd.dto.VisitorRecordDTO;
import cc.feitwnd.entity.Views;
import cc.feitwnd.entity.Visitors;
import cc.feitwnd.exception.BlockedException;
import cc.feitwnd.mapper.ViewMapper;
import cc.feitwnd.mapper.VisitorMapper;
import cc.feitwnd.result.PageResult;
import cc.feitwnd.service.VisitorService;
import cc.feitwnd.utils.IpUtil;
import cc.feitwnd.vo.VisitorRecordVO;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@Transactional
public class VisitorServiceImpl implements VisitorService {

    @Autowired
    private VisitorMapper visitorMapper;
    @Autowired
    private ViewMapper viewMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    // Redis键前缀
    public static final String VISITOR_KEY = "visitor:fingerprint:";
    private static final String RATE_LIMIT_KEY = "visitor:rate:";
    private static final String BLOCKED_KEY = "visitor:blocked:";

    /**
     * 记录访客访问信息
     * @param visitorRecordDTO
     * @param request
     * @return
     */
    public VisitorRecordVO recordVisitorViewInfo(VisitorRecordDTO visitorRecordDTO, HttpServletRequest request) {

        // 生成/获取会话Id
        String sessionId = getOrCreateSessionId(request);

        // 生成设备指纹
        String fingerprint = generateVisitorFingerprint(visitorRecordDTO,request);

        // 获取IP和地理位置信息
        String ip = IpUtil.getClientIp(request);
        Map<String, String> geoInfo = IpUtil.getGeoInfo(ip);

        // 检查访客是否在缓存中有封禁记录
        checkIfBlocked(fingerprint);

        // 检查请求频率
        checkRateLimit(fingerprint,ip);

        // 查找或创建访客记录
        Visitors visitor = findOrCreateVisitor(fingerprint,sessionId,request,ip,geoInfo);

        // 记录本次浏览
        recordView(visitor, visitorRecordDTO, ip, request);

        // 封装
        VisitorRecordVO visitorRecordVO = VisitorRecordVO.builder()
                .visitorFingerprint(fingerprint)
                .sessionId(sessionId)
                .visitorId(visitor.getId())
                .isNewVisitor(visitor.getTotalViews() <= 1)
                .build();
        return visitorRecordVO;
    }

    /**
     * 获取或创建会话ID
     * @param request
     * @return
     */
    private String getOrCreateSessionId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if(session==null){
            session = request.getSession(true);
            // 设置会话属性创建时间
            session.setAttribute("visitTime", LocalDateTime.now());
        }
        return session.getId();
    }

    /**
     * 生成访客指纹
     * @param dto
     * @param request
     * @return
     */
    private String generateVisitorFingerprint(VisitorRecordDTO dto, HttpServletRequest request) {
        // 提取更稳定的特征
        String userAgent = request.getHeader("User-Agent");

        // 简化平台信息
        String simplifiedPlatform = simplifyPlatform(dto.getPlatform());

        // 对屏幕分辨率进行分组
        String screenGroup = groupScreenResolution(dto.getScreen());

        // 处理可能为null的值
        Integer hardwareConcurrency = dto.getHardwareConcurrency() != null ?
                dto.getHardwareConcurrency() : 0;
        Integer deviceMemory = dto.getDeviceMemory() != null ?
                dto.getDeviceMemory() : 0;

        String fingerprintSource = String.format("%s|%s|%s|%s|%d|%d|%s",
                simplifiedPlatform,
                StringUtils.hasText(dto.getLanguage()) ? dto.getLanguage() : "unknown",
                StringUtils.hasText(dto.getTimezone()) ? dto.getTimezone() : "unknown",
                screenGroup,
                hardwareConcurrency,
                deviceMemory,
                extractBrowserInfo(userAgent)
        );

        return DigestUtils.md5DigestAsHex(fingerprintSource.getBytes());
    }

    /**
     * 提取浏览器核心信息
     * 从User-Agent中提取浏览器类型和主版本号
     * @param userAgent User-Agent字符串
     * @return 浏览器核心信息
     */
    private String extractBrowserInfo(String userAgent) {
        if (userAgent == null) {
            return "unknown";
        }

        String lowerUserAgent = userAgent.toLowerCase();

        // 检测浏览器类型
        String browser = "Other";
        String version = "";

        if (lowerUserAgent.contains("chrome") && !lowerUserAgent.contains("edg")) {
            browser = "Chrome";
            // 提取Chrome版本号
            int chromeIndex = lowerUserAgent.indexOf("chrome/");
            if (chromeIndex > 0) {
                version = userAgent.substring(chromeIndex + 7, chromeIndex + 12).split("\\.")[0];
            }
        } else if (lowerUserAgent.contains("firefox")) {
            browser = "Firefox";
            int firefoxIndex = lowerUserAgent.indexOf("firefox/");
            if (firefoxIndex > 0) {
                version = userAgent.substring(firefoxIndex + 8, firefoxIndex + 12).split("\\.")[0];
            }
        } else if (lowerUserAgent.contains("safari") && !lowerUserAgent.contains("chrome")) {
            browser = "Safari";
            int versionIndex = lowerUserAgent.indexOf("version/");
            if (versionIndex > 0) {
                version = userAgent.substring(versionIndex + 8, versionIndex + 12).split("\\.")[0];
            }
        } else if (lowerUserAgent.contains("edge")) {
            browser = "Edge";
            int edgeIndex = lowerUserAgent.indexOf("edg");
            if (edgeIndex > 0) {
                version = userAgent.substring(edgeIndex + 4, edgeIndex + 8).split("\\.")[0];
            }
        } else if (lowerUserAgent.contains("opera")) {
            browser = "Opera";
            int operaIndex = lowerUserAgent.indexOf("opr/");
            if (operaIndex > 0) {
                version = userAgent.substring(operaIndex + 4, operaIndex + 8).split("\\.")[0];
            }
        }

        return String.format("%s_%s", browser, version);
    }

    /**
     * 简化平台信息
     * @param platform 平台信息
     * @return 简化后的平台信息
     */
    private String simplifyPlatform(String platform) {
        if (platform == null) return "unknown";
        if (platform.contains("Win")) return "Windows";
        if (platform.contains("Mac")) return "MacOS";
        if (platform.contains("Linux")) return "Linux";
        if (platform.contains("iPhone") || platform.contains("iPad")) return "iOS";
        if (platform.contains("Android")) return "Android";
        return platform;
    }

    /**
     * 分组屏幕分辨率
     * @param screen
     * @return
     */
    private String groupScreenResolution(String screen) {
        if (screen == null) return "unknown";
        try {
            String[] parts = screen.split("x");
            if (parts.length == 2) {
                int width = Integer.parseInt(parts[0]);
                int height = Integer.parseInt(parts[1]);

                // 按常见分辨率分组
                if (width >= 3840) return "4K";
                if (width >= 2560) return "2K";
                if (width >= 1920) return "FHD";
                if (width >= 1366) return "HD";
                if (width >= 1024) return "Tablet";
                return "Mobile";
            }
        } catch (Exception e) {
            return "unknown";
        }
        return screen;
    }

    /**
     * 检查缓存是否有被封禁记录
     * @param fingerprint
     */
    private void checkIfBlocked(String fingerprint) {
        // 先检查Redis缓存
        String blockedKey = BLOCKED_KEY + fingerprint;
        Boolean isBlocked = redisTemplate.hasKey(blockedKey);

        if(isBlocked){
            throw new BlockedException(MessageConstant.VISITOR_BLOCKED);
        }
        // 检查数据库
        Visitors visitor = visitorMapper.findVisitorByFingerprint(fingerprint);
        if (visitor != null && visitor.getIsBlocked() == 1) {
            if (visitor.getExpiresAt() == null || visitor.getExpiresAt().isBefore(LocalDateTime.now())) {
                // 封禁有效，更新Redis缓存
                redisTemplate.opsForValue().set(blockedKey, "1", 1, TimeUnit.DAYS);
                throw new BlockedException(MessageConstant.VISITOR_BLOCKED);
            } else {
                // 封禁已过期，解除封禁
                visitor.setIsBlocked(0);
                visitorMapper.updateById(visitor);
            }
        }
    }

    /**
     * 检查请求频率
     * @param fingerprint
     * @param ip
     */
    private void checkRateLimit(String fingerprint, String ip) {
        // IP级别限制：每分钟60次
        String ipKey = RATE_LIMIT_KEY + "ip:" + ip;
        Long ipCount = redisTemplate.opsForValue().increment(ipKey, 1);
        if (ipCount == 1) {
            redisTemplate.expire(ipKey, 1, TimeUnit.MINUTES);
        }
        if (ipCount > 60) {
            // 自动封禁
            blockVisitor(fingerprint);
            throw new BlockedException(MessageConstant.VISITOR_BLOCKED);
        }

        // 指纹级别限制：每小时1000次
        String fpKey = RATE_LIMIT_KEY + "fp:" + fingerprint;
        Long fpCount = redisTemplate.opsForValue().increment(fpKey, 1);
        if (fpCount == 1) {
            redisTemplate.expire(fpKey, 1, TimeUnit.HOURS);
        }
        if (fpCount > 1000) {
            // 自动封禁
            blockVisitor(fingerprint);
            throw new BlockedException(MessageConstant.VISITOR_BLOCKED);
        }
    }

    /**
     * 封禁访客
     * @param fingerprint
     */
    private void blockVisitor(String fingerprint) {
        Visitors visitor = visitorMapper.findVisitorByFingerprint(fingerprint);
        if (visitor != null) {
            visitor.setIsBlocked(1);
            // 封1天
            visitor.setExpiresAt(LocalDateTime.now().plusDays(1));
            visitorMapper.updateById(visitor);

            // 更新Redis缓存
            String blockedKey = BLOCKED_KEY + fingerprint;
            redisTemplate.opsForValue().set(blockedKey, "1", 1, TimeUnit.DAYS);
            log.warn("封禁访客: fingerprint={}",
                    fingerprint);
        }
    }


    /**
     * 查找或创建访客记录
     * @param fingerprint
     * @param sessionId
     * @param request
     * @param ip
     * @param geoInfo
     * @return
     */
    private Visitors findOrCreateVisitor(String fingerprint, String sessionId,
                                         HttpServletRequest request, String ip,
                                         Map<String, String> geoInfo){
        // 尝试从Redis中获取访客信息
        String cacheKey = VISITOR_KEY + fingerprint;
        Visitors visitor = (Visitors) redisTemplate.opsForValue().get(cacheKey);

        if(visitor!=null){
            // 缓存命中,更新信息
            visitor.setSessionId(sessionId);
            visitor.setIp(ip);
            visitor.setLongitude(geoInfo.get("longitude"));
            visitor.setLatitude(geoInfo.get("latitude"));
            visitor.setCountry(geoInfo.get("country"));
            visitor.setProvince(geoInfo.get("province"));
            visitor.setCity(geoInfo.get("city"));
            visitorMapper.updateById(visitor);
            return visitor;
        }

        // 缓存未命中，通过指纹查找访客
        visitor = visitorMapper.findVisitorByFingerprint(fingerprint);

        if(visitor==null){
            // 新访客：创建记录
            visitor = Visitors.builder()
                    .fingerprint(fingerprint)
                    .sessionId(sessionId)
                    .ip(ip)
                    .userAgent(request.getHeader("User-Agent"))
                    .country(geoInfo.get("country"))
                    .province(geoInfo.get("province"))
                    .city(geoInfo.get("city"))
                    .longitude(geoInfo.get("longitude"))
                    .latitude(geoInfo.get("latitude"))
                    .firstVisitTime(LocalDateTime.now())
                    .lastVisitTime(LocalDateTime.now())
                    .totalViews(1L)
                    .isBlocked(StatusConstant.DISABLE)
                    .build();
            visitorMapper.insertVisitor(visitor);
            visitor.setId(visitor.getId());
        }else{
            // 老访客：更新信息
            visitor.setLastVisitTime(LocalDateTime.now());
            visitor.setTotalViews(visitor.getTotalViews() + 1);

            // 如果session已过期或不同，则视为新的浏览器会话
            boolean sessionExpired = !sessionId.equals(visitor.getSessionId());
            if(sessionExpired){
                visitor.setSessionId(sessionId);
            }

            // 如果IP变化，更新地理位置
            if (!ip.equals(visitor.getIp())) {
                visitor.setIp(ip);
                visitor.setLongitude(geoInfo.get("longitude"));
                visitor.setLatitude(geoInfo.get("latitude"));
                visitor.setCountry(geoInfo.get("country"));
                visitor.setProvince(geoInfo.get("province"));
                visitor.setCity(geoInfo.get("city"));
            }

            visitorMapper.updateById(visitor);
        }
        return visitor;
    }

    /**
     * 记录本次浏览
     * @param visitor
     * @param dto
     * @param ip
     * @param request
     */
    private void recordView(Visitors visitor, VisitorRecordDTO dto,
                            String ip, HttpServletRequest request) {
        Views view = Views.builder()
                .visitorId(visitor.getId())
                .pagePath(dto.getPagePath())
                .referer(dto.getReferer())
                .pageTitle(dto.getPageTitle())
                .ipAddress(ip)
                .userAgent(request.getHeader("User-Agent"))
                .viewTime(LocalDateTime.now())
                .build();
        viewMapper.insert(view);
    }

    /**
     * 分页查询访客列表
     * @param visitorPageQueryDTO
     * @return
     */
    public PageResult pageQuery(VisitorPageQueryDTO visitorPageQueryDTO) {
        PageHelper.startPage(visitorPageQueryDTO.getPage(), visitorPageQueryDTO.getPageSize());

        Page<Visitors> page = visitorMapper.pageQuery(visitorPageQueryDTO);

        return new PageResult(page.getTotal(), page.getResult());
    }
}
