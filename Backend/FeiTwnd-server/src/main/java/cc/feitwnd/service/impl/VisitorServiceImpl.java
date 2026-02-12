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
import cc.feitwnd.service.BlockService;
import cc.feitwnd.service.FingerprintService;
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

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class VisitorServiceImpl implements VisitorService {

    @Autowired
    private VisitorMapper visitorMapper;
    @Autowired
    private ViewMapper viewMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private FingerprintService fingerprintService;
    @Autowired
    private BlockService blockService;

    // Redis键前缀
    public static final String VISITOR_KEY = "visitor:fingerprint:";

    /**
     * 记录访客访问信息
     * @param visitorRecordDTO
     * @param request
     * @return
     */
    @Transactional
    public VisitorRecordVO recordVisitorViewInfo(VisitorRecordDTO visitorRecordDTO, HttpServletRequest request) {

        // 生成/获取会话Id
        String sessionId = getOrCreateSessionId(request);

        // 生成设备指纹
        String fingerprint = fingerprintService.generateVisitorFingerprint(visitorRecordDTO,request);

        // 获取IP和地理位置信息
        String ip = IpUtil.getClientIp(request);
        Map<String, String> geoInfo = IpUtil.getGeoInfo(ip);

        // 检查访客是否在缓存中有封禁记录
        blockService.checkIfBlocked(fingerprint);

        // 检查请求频率
        blockService.checkRateLimit(fingerprint,ip);

        // 查找或创建访客记录
        Visitors visitor = findOrCreateVisitor(fingerprint,sessionId,request,ip,geoInfo);

        // 记录本次浏览
        recordView(visitor, visitorRecordDTO, ip, request);

        // 封装VO
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

    /**
     * 批量封禁访客
     * @param ids
     */
    public void batchBlock(List<Long> ids) {
        visitorMapper.batchBlock(ids);
    }

    /**
     * 批量解封访客
     * @param ids
     */
    public void batchUnblock(List<Long> ids) {
        visitorMapper.batchUnblock(ids);
    }
}
