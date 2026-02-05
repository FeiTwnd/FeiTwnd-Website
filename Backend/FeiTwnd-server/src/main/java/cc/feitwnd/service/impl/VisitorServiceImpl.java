package cc.feitwnd.service.impl;

import cc.feitwnd.constant.StatusConstant;
import cc.feitwnd.dto.VisitorRecordDTO;
import cc.feitwnd.entity.Views;
import cc.feitwnd.entity.Visitors;
import cc.feitwnd.mapper.ViewMapper;
import cc.feitwnd.mapper.VisitorMapper;
import cc.feitwnd.service.VisitorService;
import cc.feitwnd.utils.IpUtil;
import cc.feitwnd.vo.VisitorRecordVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class VisitorServiceImpl implements VisitorService {

    @Autowired
    private VisitorMapper visitorMapper;
    @Autowired
    private ViewMapper viewMapper;

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
        String visitorFingerprint = generateVisitorFingerprint(visitorRecordDTO,request);

        // 获取IP和地理位置信息
        String ip = IpUtil.getClientIp(request);
        Map<String, String> geoInfo = IpUtil.getGeoInfo(ip);

        // 查找或创建访客记录
        Visitors visitor = findOrCreateVisitor(visitorFingerprint,sessionId,request,ip,geoInfo);

        // 记录本次浏览
        recordView(visitor, visitorRecordDTO, ip, request);

        // 封装
        VisitorRecordVO visitorRecordVO = VisitorRecordVO.builder()
                .visitorFingerprint(visitorFingerprint)
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
        // 组合浏览器特征生成指纹
        String fingerprintSource = String.format("%s-%s-%s-%s-%s-%s-%s-%s",
                dto.getScreen(),
                dto.getTimezone(),
                dto.getLanguage(),
                dto.getPlatform(),
                dto.getCookiesEnabled(),
                dto.getDeviceMemory(),
                dto.getHardwareConcurrency(),
                request.getHeader("User-Agent")
        );

        // 使用MD5生成固定长度的指纹
        return DigestUtils.md5DigestAsHex(fingerprintSource.getBytes());
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
        // 通过指纹查找访客
        Visitors visitor = visitorMapper.findVisitorByFingerprint(fingerprint);

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
                .ipAddress(ip)
                .userAgent(request.getHeader("User-Agent"))
                .viewTime(LocalDateTime.now())
                .build();
        viewMapper.insert(view);
    }
}
